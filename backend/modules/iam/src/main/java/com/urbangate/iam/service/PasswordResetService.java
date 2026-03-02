// Copyright (c) UrbanGate
package com.urbangate.iam.service;

import com.urbangate.iam.repository.impl.ResetTokenServiceImpl;
import com.urbangate.iam.tenant.TenantDiscoveryKeyCloakService;
import com.urbangate.shared.enums.ExceptionResponse;
import com.urbangate.shared.exceptions.InvalidResetTokenException;
import com.urbangate.shared.exceptions.UserNameNotFoundException;
import com.urbangate.shared.service.EmailService;
import java.security.SecureRandom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

  private final TenantDiscoveryKeyCloakService tenantDiscoveryKeyCloakService;
  private final ResetTokenServiceImpl tokenService;
  private final EmailService emailService;

  public void initiatePasswordReset(String email) {
    List<UserRepresentation> users = getRealmResource().users().searchByEmail(email, true);

    if (users.isEmpty()) {
      log.warn("Password reset requested for unknown email: {}", email);
      return;
    }

    String code = generateSecureCode();
    // TODOX MAKE ASYNCHORONOUS TO REDUCE RESPONSE TIME
    tokenService.save(email, code);

    // TODOX SEND CODE TO EMAIL & MOBILE
    emailService.sendEmail(email, code);

    log.info("Password reset code issued for: {}", email);
  }

  public void confirmPasswordReset(String email, String code, String newPassword) {
    if (!tokenService.validate(code, email)) {
      throw new InvalidResetTokenException(ExceptionResponse.INVALID_RESET_CODE);
    }

    List<UserRepresentation> users = getRealmResource().users().searchByEmail(email, true);

    if (users.isEmpty()) {
      throw new UserNameNotFoundException(ExceptionResponse.USER_NAME_NOTFOUND);
    }

    String userId = users.get(0).getId();
    setKeycloakPassword(userId, newPassword);

    tokenService.evict(email);
    log.info("Password successfully reset for user: {}", userId);
  }

  private void setKeycloakPassword(String userId, String newPassword) {
    CredentialRepresentation credential = new CredentialRepresentation();
    credential.setType(CredentialRepresentation.PASSWORD);
    credential.setValue(newPassword);
    credential.setTemporary(false);

    getRealmResource().users().get(userId).resetPassword(credential);
  }

  private String generateSecureCode() {
    return String.format("%06d", new SecureRandom().nextInt(999_999));
  }

  private RealmResource getRealmResource() {
    return tenantDiscoveryKeyCloakService.currentRealm();
  }
}
