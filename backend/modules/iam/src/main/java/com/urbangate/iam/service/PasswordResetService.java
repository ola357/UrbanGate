// Copyright (c) UrbanGate
package com.urbangate.iam.service;

import com.urbangate.iam.config.PasswordResetProperties;
import com.urbangate.iam.domain.entity.PasswordReset;
import com.urbangate.iam.domain.entity.Resident;
import com.urbangate.iam.keycloak.KeycloakAdminClient;
import com.urbangate.iam.repository.PasswordResetRepository;
import com.urbangate.iam.repository.ResidentRepository;
import com.urbangate.shared.tenant.TenantContext;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetService {
  private final PasswordResetRepository passwordResetRepository;
  private final ResidentRepository residentRepository;
  private final PasswordResetProperties passwordResetProperties;
  private final KeycloakAdminClient keycloakAdminClient;
  private final LoginAttemptService loginAttemptService;
  private final SecureRandom random = new SecureRandom();

  public PasswordResetService(
      PasswordResetRepository passwordResetRepository,
      ResidentRepository residentRepository,
      PasswordResetProperties passwordResetProperties,
      KeycloakAdminClient keycloakAdminClient,
      LoginAttemptService loginAttemptService) {
    this.passwordResetRepository = passwordResetRepository;
    this.residentRepository = residentRepository;
    this.passwordResetProperties = passwordResetProperties;
    this.keycloakAdminClient = keycloakAdminClient;
    this.loginAttemptService = loginAttemptService;
  }

  @Transactional
  public PasswordResetCode issueResetCode(UUID residentId) {
    UUID estateId = TenantContext.requireTenantId();
    Resident resident =
        residentRepository
            .findByEstateIdAndId(estateId, residentId)
            .orElseThrow(() -> new IllegalArgumentException("Resident not found for estate"));
    return issueResetCodeInternal(resident);
  }

  @Transactional
  public PasswordResetCode issueResetCodeForPhone(String phone) {
    UUID estateId = TenantContext.requireTenantId();
    Resident resident =
        residentRepository
            .findByEstateIdAndPhone(estateId, phone)
            .orElseThrow(() -> new IllegalArgumentException("Resident not found for estate"));
    return issueResetCodeInternal(resident);
  }

  @Transactional
  public void confirmReset(UUID residentId, String code, String newPassword) {
    UUID estateId = TenantContext.requireTenantId();
    Resident resident =
        residentRepository
            .findByEstateIdAndId(estateId, residentId)
            .orElseThrow(() -> new IllegalArgumentException("Resident not found for estate"));
    confirmResetInternal(resident, code, newPassword);
  }

  @Transactional
  public void confirmResetForPhone(String phone, String code, String newPassword) {
    UUID estateId = TenantContext.requireTenantId();
    Resident resident =
        residentRepository
            .findByEstateIdAndPhone(estateId, phone)
            .orElseThrow(() -> new IllegalArgumentException("Resident not found for estate"));
    confirmResetInternal(resident, code, newPassword);
  }

  private PasswordResetCode issueResetCodeInternal(Resident resident) {
    String rawCode = generateNumericCode(passwordResetProperties.getCodeLength());
    String hashedCode = hashCode(rawCode);

    PasswordReset reset = new PasswordReset();
    reset.setResident(resident);
    reset.setCode(hashedCode);
    reset.setExpiresAt(Instant.now().plus(passwordResetProperties.getTtl()));
    reset.setAttempts(0);

    PasswordReset saved = passwordResetRepository.save(reset);
    return new PasswordResetCode(saved.getId(), rawCode, saved.getExpiresAt());
  }

  private void confirmResetInternal(Resident resident, String code, String newPassword) {
    PasswordReset reset =
        passwordResetRepository
            .findFirstByResidentIdAndConsumedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(
                resident.getId(), Instant.now())
            .orElseThrow(() -> new IllegalStateException("No valid password reset request"));

    String hashedCode = hashCode(code);
    if (!hashedCode.equals(reset.getCode())) {
      int attempts = reset.getAttempts() + 1;
      reset.setAttempts(attempts);
      if (attempts >= passwordResetProperties.getMaxAttempts()) {
        reset.setConsumedAt(Instant.now());
      }
      passwordResetRepository.save(reset);
      throw new IllegalArgumentException("Invalid password reset code");
    }

    if (resident.getKeycloakUserId() == null || resident.getKeycloakUserId().isBlank()) {
      throw new IllegalStateException("Resident is missing Keycloak user id");
    }

    keycloakAdminClient.updatePassword(resident.getKeycloakUserId(), newPassword);

    reset.setConsumedAt(Instant.now());
    passwordResetRepository.save(reset);
    loginAttemptService.clearFailures(resident.getId());
  }

  private String generateNumericCode(int length) {
    if (length <= 0) {
      throw new IllegalArgumentException("Reset code length must be positive");
    }
    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      builder.append(random.nextInt(10));
    }
    return builder.toString();
  }

  private String hashCode(String rawCode) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashed = digest.digest(rawCode.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hashed);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 not available", e);
    }
  }
}
