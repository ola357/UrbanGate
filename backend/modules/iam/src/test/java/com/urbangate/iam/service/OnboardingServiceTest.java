// Copyright (c) UrbanGate
package com.urbangate.iam.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.urbangate.iam.config.ActivationCodeProperties;
import com.urbangate.iam.domain.entity.ActivationCode;
import com.urbangate.iam.domain.entity.Estate;
import com.urbangate.iam.domain.entity.Resident;
import com.urbangate.iam.domain.enums.ResidentStatus;
import com.urbangate.iam.notification.EmailNotificationService;
import com.urbangate.iam.repository.ActivationCodeRepository;
import com.urbangate.iam.repository.ResidentRepository;
import com.urbangate.shared.tenant.TenantContext;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OnboardingServiceTest {

  @Mock private ActivationCodeRepository activationCodeRepository;
  @Mock private ResidentRepository residentRepository;
  @Mock private EmailNotificationService emailNotificationService;
  @Mock private OnboardingService self;

  private ActivationCodeProperties activationCodeProperties;
  private OnboardingService service;

  @BeforeEach
  void setUp() {
    activationCodeProperties = new ActivationCodeProperties();
    activationCodeProperties.setLength(6);
    activationCodeProperties.setAlphabet("ABCDEF");
    activationCodeProperties.setTtl(Duration.ofHours(2));
    service =
        new OnboardingService(
            activationCodeRepository,
            residentRepository,
            activationCodeProperties,
            emailNotificationService,
            self);
  }

  @AfterEach
  void tearDown() {
    TenantContext.clear();
  }

  @Test
  void issueActivationCodeCreatesCodeForResident() {
    UUID estateId = UUID.randomUUID();
    UUID residentId = UUID.randomUUID();
    TenantContext.setTenant(estateId.toString());

    Estate estate = new Estate();
    estate.setId(estateId);
    Resident resident = new Resident();
    resident.setId(residentId);
    resident.setEstate(estate);

    when(residentRepository.findByEstateIdAndId(estateId, residentId))
        .thenReturn(Optional.of(resident));
    when(activationCodeRepository.findByCode(any())).thenReturn(Optional.empty());
    when(activationCodeRepository.save(any(ActivationCode.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Instant before = Instant.now();
    ActivationCode activationCode = service.issueActivationCode(residentId);
    Instant after = Instant.now();

    assertEquals(resident, activationCode.getResident());
    assertEquals(estate, activationCode.getEstate());
    assertEquals(6, activationCode.getCode().length());
    assertTrue(
        activationCode.getCode().chars().allMatch(c -> "ABCDEF".indexOf(c) >= 0),
        "Code should use configured alphabet");
    assertTrue(
        !activationCode.getExpiresAt().isBefore(before)
            && !activationCode
                .getExpiresAt()
                .isAfter(after.plus(activationCodeProperties.getTtl())),
        "Expiry should be within TTL window");
  }

  @Test
  void activateResidentMarksActiveAndSendsEmail() {
    UUID estateId = UUID.randomUUID();
    TenantContext.setTenant(estateId.toString());

    Estate estate = new Estate();
    estate.setId(estateId);
    Resident resident = new Resident();
    resident.setId(UUID.randomUUID());
    resident.setEstate(estate);
    resident.setStatus(ResidentStatus.PENDING);

    ActivationCode code = new ActivationCode();
    code.setCode("ABC123");
    code.setEstate(estate);
    code.setResident(resident);
    code.setExpiresAt(Instant.now().plus(Duration.ofHours(1)));

    when(activationCodeRepository.findByCodeAndEstateId(eq("ABC123"), eq(estateId)))
        .thenReturn(Optional.of(code));
    when(activationCodeRepository.save(any(ActivationCode.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(residentRepository.save(any(Resident.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Resident result = service.activateResident("ABC123", "kc-123");

    assertEquals(ResidentStatus.ACTIVE, result.getStatus());
    assertNotNull(result.getActivatedAt());
    assertEquals("kc-123", result.getKeycloakUserId());
    verify(emailNotificationService).queueResidentOnboardedEmail(resident);
  }
}
