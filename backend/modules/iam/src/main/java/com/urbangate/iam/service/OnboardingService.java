// Copyright (c) UrbanGate
package com.urbangate.iam.service;

import com.urbangate.iam.config.ActivationCodeProperties;
import com.urbangate.iam.domain.entity.ActivationCode;
import com.urbangate.iam.domain.entity.Resident;
import com.urbangate.iam.domain.enums.ResidentStatus;
import com.urbangate.iam.notification.EmailNotificationService;
import com.urbangate.iam.repository.ActivationCodeRepository;
import com.urbangate.iam.repository.ResidentRepository;
import com.urbangate.shared.tenant.TenantContext;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OnboardingService {
  private static final int UNIQUE_CODE_RETRIES = 6;
  private static final String RESIDENT_NOT_FOUND = "Resident not found for estate";

  private final ActivationCodeRepository activationCodeRepository;
  private final ResidentRepository residentRepository;
  private final ActivationCodeProperties activationCodeProperties;
  private final EmailNotificationService emailNotificationService;
  private final OnboardingService self;
  private final SecureRandom random = new SecureRandom();

  public OnboardingService(
      ActivationCodeRepository activationCodeRepository,
      ResidentRepository residentRepository,
      ActivationCodeProperties activationCodeProperties,
      EmailNotificationService emailNotificationService,
      @Lazy OnboardingService self) {
    this.activationCodeRepository = activationCodeRepository;
    this.residentRepository = residentRepository;
    this.activationCodeProperties = activationCodeProperties;
    this.emailNotificationService = emailNotificationService;
    this.self = self;
  }

  @Transactional
  public ActivationCode issueActivationCode(UUID residentId) {
    UUID estateId = TenantContext.requireTenantId();
    Resident resident =
        residentRepository
            .findByEstateIdAndId(estateId, residentId)
            .orElseThrow(() -> new IllegalArgumentException(RESIDENT_NOT_FOUND));

    String code = generateUniqueCode();
    ActivationCode activationCode = new ActivationCode();
    activationCode.setEstate(resident.getEstate());
    activationCode.setResident(resident);
    activationCode.setCode(code);
    activationCode.setExpiresAt(Instant.now().plus(activationCodeProperties.getTtl()));

    return activationCodeRepository.save(activationCode);
  }

  @Transactional
  public Resident activateResident(String activationCode) {
    return self.activateResident(activationCode, null);
  }

  @Transactional
  public Resident activateResident(String activationCode, String keycloakUserId) {
    UUID estateId = TenantContext.requireTenantId();
    ActivationCode storedCode =
        activationCodeRepository
            .findByCodeAndEstateId(activationCode, estateId)
            .orElseThrow(() -> new IllegalArgumentException("Activation code not found"));

    Instant now = Instant.now();
    if (storedCode.getUsedAt() != null) {
      throw new IllegalStateException("Activation code already used");
    }
    if (storedCode.getExpiresAt().isBefore(now)) {
      throw new IllegalStateException("Activation code expired");
    }

    Resident resident = storedCode.getResident();
    if (resident.getStatus() == ResidentStatus.ACTIVE) {
      throw new IllegalStateException("Resident already active");
    }

    storedCode.setUsedAt(now);
    resident.setStatus(ResidentStatus.ACTIVE);
    resident.setActivatedAt(now);
    if (keycloakUserId != null && !keycloakUserId.isBlank()) {
      resident.setKeycloakUserId(keycloakUserId);
    }

    activationCodeRepository.save(storedCode);
    Resident saved = residentRepository.save(resident);
    emailNotificationService.queueResidentOnboardedEmail(saved);
    return saved;
  }

  private String generateUniqueCode() {
    for (int i = 0; i < UNIQUE_CODE_RETRIES; i++) {
      String code = generateCode();
      if (activationCodeRepository.findByCode(code).isEmpty()) {
        return code;
      }
    }
    throw new IllegalStateException("Unable to generate a unique activation code");
  }

  private String generateCode() {
    String alphabet = activationCodeProperties.getAlphabet();
    int length = activationCodeProperties.getLength();
    if (alphabet == null || alphabet.isBlank()) {
      throw new IllegalStateException("Activation code alphabet is not configured");
    }
    if (length <= 0) {
      throw new IllegalStateException("Activation code length must be greater than zero");
    }

    StringBuilder builder = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int index = random.nextInt(alphabet.length());
      builder.append(alphabet.charAt(index));
    }
    return builder.toString();
  }
}
