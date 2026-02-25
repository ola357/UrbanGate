// Copyright (c) UrbanGate
package com.urbangate.iam.service;

import static com.urbangate.iam.tenant.TenantResolutionFilter.resolveTenant;

import com.urbangate.iam.dto.request.PasswordRequest;
import com.urbangate.iam.dto.request.ResidentOnboardingRequest;
import com.urbangate.iam.dto.response.PasswordResponse;
import com.urbangate.iam.dto.response.ResidentOnboardingResponse;
import com.urbangate.iam.dto.response.UserResponse;
import com.urbangate.iam.entity.ActivationCode;
import com.urbangate.iam.entity.TenantConfiguration;
import com.urbangate.iam.repository.ActivationCodeRepository;
import com.urbangate.iam.repository.TenantConfigurationRepository;
import com.urbangate.iam.repository.impl.TenantConfigurationRedisImpl;
import com.urbangate.iam.tenant.TenantDiscoveryKeyCloakService;
import com.urbangate.shared.enums.ExceptionResponse;
import com.urbangate.shared.exceptions.ConflictException;
import com.urbangate.shared.exceptions.DataBaseOperationException;
import com.urbangate.shared.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakUserService {

  private final Keycloak keycloak;
  private final TenantDiscoveryKeyCloakService tenantDiscoveryKeyCloakService;
  private final ActivationCodeRepository activationCodeRepository;
  private final TenantConfigurationRepository tenantConfigurationRepository;
  private final TenantConfigurationRedisImpl tenantConfigurationRedisImpl;

  @Value("${activation-code.ttl-in-minutes:5}")
  private int activationCodeTtlInMinutes;

  // ── Registration ───────────────────────────────────────────────────────────

  @Transactional
  public ResidentOnboardingResponse register(
      ResidentOnboardingRequest request, HttpServletRequest httpServletRequest) {

    log.info("Registering new user: {}", request.phoneNumber());

    RealmResource realm = getRealmResource();

    List<UserRepresentation> existing =
        getRealmResource().users().searchByAttributes("phone_number:" + request.phoneNumber());
    log.info("Existing users: {}", existing);
    if (!existing.isEmpty()) {
      throw new ConflictException(ExceptionResponse.PHONE_NUMBER_ALREADY_REGISTERED);
    }

    if (!realm.users().searchByEmail(request.email(), true).isEmpty()) {
      throw new ConflictException(ExceptionResponse.EMAIL_ALREADY_REGISTERED);
    }
    UserRepresentation user = new UserRepresentation();
    user.setUsername(request.phoneNumber());
    user.setEmail(request.email());
    user.setFirstName(request.firstName());
    user.setLastName(request.lastName());
    user.setEnabled(true);
    user.setEmailVerified(false);

    // Store phone as a custom attribute
    user.setAttributes(
        Map.of(
            "phone_number", List.of(request.phoneNumber()),
            "gender", List.of(request.gender()),
            "unit_address", List.of(request.unitAddress())));

    // Create user in Keycloak
    try (Response response = realm.users().create(user)) {
      if (response.getStatus() != 201) {
        String body = response.readEntity(String.class);
        log.error("Failed to create user. Status: {} Body: {}", response.getStatus(), body);
        throw new RuntimeException("Failed to create user in Keycloak: " + body);
      }

      String userId = extractUserIdFromLocation(response);
      log.info("User created with ID: {}", userId);

      // Assign default role
      String roleName =
          (request.role() != null && !request.role().isBlank())
              ? request.role().toLowerCase()
              : "resident";
      assignRealmRole(userId, roleName);

      String code = saveActivationCode(userId, httpServletRequest);

      return new ResidentOnboardingResponse(userId, code);
    }
  }

  private String saveActivationCode(String userId, HttpServletRequest httpServletRequest) {
    ActivationCode activationCode = new ActivationCode();
    activationCode.setTtlInHours(activationCodeTtlInMinutes);
    activationCode.setUserId(userId);

    String token = generateActivationCode(httpServletRequest);
    activationCode.setCode(token);
    activationCodeRepository.insert(activationCode);
    return token;
  }

  private String generateActivationCode(HttpServletRequest httpServletRequest) {
    String realm = "tenant_" + resolveTenant(httpServletRequest);

    TenantConfiguration tenantConfiguration =
        tenantConfigurationRedisImpl
            .findById(realm)
            .orElseGet(
                () ->
                    tenantConfigurationRepository
                        .findByRealm(realm)
                        .orElseThrow(
                            () ->
                                new ResourceNotFoundException(
                                    ExceptionResponse.UNABLE_TO_FETCH_RECORD)));

    int randomPrefix = generateUniqueCode();
    return tenantConfiguration.getEstateCode() + "-" + randomPrefix;
  }

  private int generateUniqueCode() {
    return ThreadLocalRandom.current().nextInt(1000, 10000);
  }

  public PasswordResponse setUpPassword(PasswordRequest request) {

    CredentialRepresentation credential = new CredentialRepresentation();
    credential.setType(CredentialRepresentation.PASSWORD);
    credential.setValue(request.password());
    credential.setTemporary(false);

    getRealmResource().users().get(request.userId()).resetPassword(credential);
    boolean revoked = activationCodeRepository.revokeCode(request.activationToken());
    if (!revoked) {
      throw new DataBaseOperationException(ExceptionResponse.UNABLE_TO_REVOKE_ACTIVATION_CODE);
    }

    return new PasswordResponse(request.userId(), true);
  }

  public UserResponse getUserById(String userId) {
    UserRepresentation user = getRealmResource().users().get(userId).toRepresentation();
    return toUserResponse(userId, user);
  }

  public UserResponse getUserByActivationCode(String code) {
    ActivationCode activationCode =
        activationCodeRepository
            .findByCode(code)
            .orElseThrow(
                () -> new ResourceNotFoundException(ExceptionResponse.UNABLE_TO_FETCH_RECORD));
    log.info("Activation code found: {}", activationCode.getUserId());
    UserRepresentation user =
        getRealmResource().users().get(activationCode.getUserId().trim()).toRepresentation();
    return toUserResponse(user.getId(), user);
  }

  public UserResponse getUserByUsername(String username) {
    List<UserRepresentation> users = getRealmResource().users().search(username, true);
    if (users.isEmpty()) {
      throw new ResourceNotFoundException(ExceptionResponse.USER_NAME_NOTFOUND);
    }
    ;
    UserRepresentation user = users.get(0);
    return toUserResponse(user.getId(), user);
  }

  public List<UserResponse> getAllUsers(int first, int max) {
    return getRealmResource().users().list(first, max).stream()
        .map(u -> toUserResponse(u.getId(), u))
        .collect(Collectors.toList());
  }

  public List<UserResponse> searchUsers(String query) {
    return getRealmResource().users().search(query, 0, 50).stream()
        .map(u -> toUserResponse(u.getId(), u))
        .collect(Collectors.toList());
  }

  public UserResponse updateUser(String userId, String firstName, String lastName, String email) {
    UserResource userResource = getRealmResource().users().get(userId);
    UserRepresentation user = userResource.toRepresentation();

    if (firstName != null) {
      user.setFirstName(firstName);
    }
    if (lastName != null) {
      user.setLastName(lastName);
    }
    if (email != null) {
      user.setEmail(email);
    }

    userResource.update(user);
    log.info("Updated user: {}", userId);
    return toUserResponse(userId, user);
  }

  // ── Password ───────────────────────────────────────────────────────────────

  public void resetPassword(String userId, String newPassword, boolean temporary) {
    CredentialRepresentation credential = new CredentialRepresentation();
    credential.setType(CredentialRepresentation.PASSWORD);
    credential.setValue(newPassword);
    credential.setTemporary(temporary);
    getRealmResource().users().get(userId).resetPassword(credential);
    log.info("Password reset for user: {}", userId);
  }

  /**
   * Triggers Keycloak to send a password-reset email to the user. The email contains a time-limited
   * link that redirects to Keycloak's reset form.
   */
  public void sendPasswordResetEmail(String email) {
    List<UserRepresentation> users = getRealmResource().users().searchByEmail(email, true);
    if (users.isEmpty()) {
      // Security: don't reveal whether the email exists
      log.info("Password reset requested for unknown email: {}", email);
      return;
    }
    String userId = users.get(0).getId();
    getRealmResource().users().get(userId).executeActionsEmail(List.of("UPDATE_PASSWORD"));
    log.info("Password reset email sent for user: {}", userId);
  }

  /**
   * Admin-sets a user's password directly (for the reset-password flow with token). In production,
   * integrate with Keycloak's built-in action token or implement a custom provider.
   */
  public void adminResetPassword(String userId, String newPassword) {
    resetPassword(userId, newPassword, false);
  }

  // ── Email Verification ─────────────────────────────────────────────────────

  public void sendVerificationEmail(String userId) {
    try {
      getRealmResource().users().get(userId).executeActionsEmail(List.of("VERIFY_EMAIL"));
      log.info("Verification email sent to user: {}", userId);
    } catch (Exception e) {
      log.warn("Could not send verification email for user {}: {}", userId, e.getMessage());
    }
  }

  // ── Roles ──────────────────────────────────────────────────────────────────

  public void assignRealmRole(String userId, String roleName) {
    RolesResource rolesResource = getRealmResource().roles();
    try {
      RoleRepresentation role = rolesResource.get(roleName).toRepresentation();
      getRealmResource().users().get(userId).roles().realmLevel().add(List.of(role));
      log.info("Assigned realm role '{}' to user: {}", roleName, userId);
    } catch (Exception e) {
      log.warn("Role '{}' not found in realm, skipping assignment: {}", roleName, e.getMessage());
    }
  }

  public void removeRealmRole(String userId, String roleName) {
    RoleRepresentation role = getRealmResource().roles().get(roleName).toRepresentation();
    getRealmResource().users().get(userId).roles().realmLevel().remove(List.of(role));
    log.info("Removed realm role '{}' from user: {}", roleName, userId);
  }

  public List<String> getUserRealmRoles(String userId) {
    return getRealmResource().users().get(userId).roles().realmLevel().listAll().stream()
        .map(RoleRepresentation::getName)
        .filter(
            r ->
                !r.startsWith("default-roles")
                    && !r.equals("offline_access")
                    && !r.equals("uma_authorization"))
        .collect(Collectors.toList());
  }

  // ── Private helpers ────────────────────────────────────────────────────────

  private RealmResource getRealmResource() {
    return tenantDiscoveryKeyCloakService.currentRealm();
  }

  private String extractUserIdFromLocation(Response response) {
    String location = response.getHeaderString("Location");
    if (location == null) {
      throw new RuntimeException("Keycloak did not return a Location header");
    }
    return location.substring(location.lastIndexOf("/") + 1);
  }

  private UserResponse toUserResponse(String userId, UserRepresentation user) {

    return new UserResponse(
        userId,
        user.getUsername(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getAttributes());
  }

  @Scheduled(fixedDelayString = "${activation-code.revoke-schedule-time:6000}")
  public void scheduleActivationCodeRevoke() {
    List<ActivationCode> activationCodes = activationCodeRepository.findAll();
    if (activationCodes.isEmpty()) {
      log.warn("No activation codes found");
    }
    activationCodes.forEach(
        activationCode -> {
          LocalDateTime now = LocalDateTime.now();
          LocalDateTime timestamp =
              activationCode
                  .getCreatedOn()
                  .toLocalDateTime()
                  .plusHours(activationCode.getTtlInHours());
          Boolean isPastTtl = now.isAfter(timestamp);
          log.info("activation code found -{}", activationCode);
          if (!activationCode.isRevoked() && isPastTtl) {
            activationCodeRepository.revokeCode(activationCode.getCode());
            log.info("Revoked activation code {}", activationCode.getCode());
          }
        });
  }
}
