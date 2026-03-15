// Copyright (c) UrbanGate
package com.urbangate.iam.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.urbangate.iam.config.PasswordResetProperties;
import com.urbangate.iam.domain.entity.PasswordReset;
import com.urbangate.iam.domain.entity.Resident;
import com.urbangate.iam.keycloak.KeycloakAdminClient;
import com.urbangate.iam.repository.PasswordResetRepository;
import com.urbangate.iam.repository.ResidentRepository;
import com.urbangate.shared.tenant.TenantContext;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

  @Mock private PasswordResetRepository passwordResetRepository;
  @Mock private ResidentRepository residentRepository;
  @Mock private KeycloakAdminClient keycloakAdminClient;
  @Mock private LoginAttemptService loginAttemptService;

  private PasswordResetProperties properties;
  private PasswordResetService service;

  @BeforeEach
  void setUp() {
    properties = new PasswordResetProperties();
    properties.setTtl(Duration.ofMinutes(10));
    properties.setCodeLength(6);
    properties.setMaxAttempts(1);
    service =
        new PasswordResetService(
            passwordResetRepository,
            residentRepository,
            properties,
            keycloakAdminClient,
            loginAttemptService);
  }

  @AfterEach
  void tearDown() {
    TenantContext.clear();
  }

  @Test
  void issueResetCodeGeneratesNumericCode() {
    UUID estateId = UUID.randomUUID();
    UUID residentId = UUID.randomUUID();
    TenantContext.setTenant(estateId.toString());

    Resident resident = new Resident();
    resident.setId(residentId);

    when(residentRepository.findByEstateIdAndId(estateId, residentId))
        .thenReturn(Optional.of(resident));
    when(passwordResetRepository.save(any(PasswordReset.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    PasswordResetCode code = service.issueResetCode(residentId);

    assertEquals(6, code.code().length());
    assertTrue(code.code().chars().allMatch(Character::isDigit));
    assertNotNull(code.expiresAt());
  }

  @Test
  void confirmResetIncrementsAttemptsOnInvalidCode() {
    UUID estateId = UUID.randomUUID();
    UUID residentId = UUID.randomUUID();
    TenantContext.setTenant(estateId.toString());

    Resident resident = new Resident();
    resident.setId(residentId);
    when(residentRepository.findByEstateIdAndId(estateId, residentId))
        .thenReturn(Optional.of(resident));

    PasswordReset reset = new PasswordReset();
    reset.setResident(resident);
    reset.setCode(hash("123456"));
    reset.setExpiresAt(Instant.now().plusSeconds(60));
    reset.setAttempts(0);
    when(passwordResetRepository
            .findFirstByResidentIdAndConsumedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(
                eq(residentId), any()))
        .thenReturn(Optional.of(reset));

    assertThrows(
        IllegalArgumentException.class,
        () -> service.confirmReset(residentId, "000000", "new-password"));

    ArgumentCaptor<PasswordReset> captor = ArgumentCaptor.forClass(PasswordReset.class);
    verify(passwordResetRepository).save(captor.capture());
    PasswordReset saved = captor.getValue();
    assertEquals(1, saved.getAttempts());
    assertNotNull(saved.getConsumedAt());
    verify(keycloakAdminClient, never()).updatePassword(any(), any());
    verify(loginAttemptService, never()).clearFailures(any());
  }

  @Test
  void confirmResetUpdatesPasswordOnValidCode() {
    UUID estateId = UUID.randomUUID();
    UUID residentId = UUID.randomUUID();
    TenantContext.setTenant(estateId.toString());

    Resident resident = new Resident();
    resident.setId(residentId);
    resident.setKeycloakUserId("kc-1");
    when(residentRepository.findByEstateIdAndId(estateId, residentId))
        .thenReturn(Optional.of(resident));

    PasswordReset reset = new PasswordReset();
    reset.setResident(resident);
    reset.setCode(hash("111111"));
    reset.setExpiresAt(Instant.now().plusSeconds(60));
    reset.setAttempts(0);
    when(passwordResetRepository
            .findFirstByResidentIdAndConsumedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(
                eq(residentId), any()))
        .thenReturn(Optional.of(reset));
    when(passwordResetRepository.save(any(PasswordReset.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    service.confirmReset(residentId, "111111", "new-password");

    verify(keycloakAdminClient).updatePassword("kc-1", "new-password");
    verify(loginAttemptService).clearFailures(residentId);
    assertNotNull(reset.getConsumedAt());
  }

  private String hash(String rawCode) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashed = digest.digest(rawCode.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hashed);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
