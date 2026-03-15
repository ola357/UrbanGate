// Copyright (c) UrbanGate
package com.urbangate.iam.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.urbangate.iam.config.LockoutProperties;
import com.urbangate.iam.domain.entity.LoginAttempt;
import com.urbangate.iam.domain.entity.Resident;
import com.urbangate.iam.repository.LoginAttemptRepository;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceTest {

  @Mock private LoginAttemptRepository loginAttemptRepository;

  private LoginAttemptService service;

  @BeforeEach
  void setUp() {
    LockoutProperties props = new LockoutProperties();
    props.setWindow(Duration.ofMinutes(15));
    props.setMaxFailures(3);
    service = new LoginAttemptService(loginAttemptRepository, props);
  }

  @Test
  void recordAttemptSavesAttempt() {
    Resident resident = new Resident();
    service.recordAttempt(resident, false, "bad password", "127.0.0.1", "agent");

    ArgumentCaptor<LoginAttempt> captor = ArgumentCaptor.forClass(LoginAttempt.class);
    verify(loginAttemptRepository).save(captor.capture());

    LoginAttempt attempt = captor.getValue();
    assertEquals(resident, attempt.getResident());
    assertFalse(attempt.isSuccess());
    assertEquals("bad password", attempt.getFailureReason());
    assertEquals("127.0.0.1", attempt.getIpAddress());
    assertEquals("agent", attempt.getUserAgent());
  }

  @Test
  void isLockedReturnsTrueWhenFailuresAtThreshold() {
    UUID residentId = UUID.randomUUID();
    when(loginAttemptRepository.countByResidentIdAndSuccessIsFalseAndAttemptedAtAfter(
            eq(residentId), any()))
        .thenReturn(3L);

    assertTrue(service.isLocked(residentId));
  }

  @Test
  void ensureNotLockedThrowsWhenLocked() {
    UUID residentId = UUID.randomUUID();
    when(loginAttemptRepository.countByResidentIdAndSuccessIsFalseAndAttemptedAtAfter(
            eq(residentId), any()))
        .thenReturn(3L);

    assertThrows(IllegalStateException.class, () -> service.ensureNotLocked(residentId));
  }

  @Test
  void clearFailuresDeletesFailures() {
    UUID residentId = UUID.randomUUID();
    service.clearFailures(residentId);
    verify(loginAttemptRepository).deleteByResidentIdAndSuccessIsFalse(residentId);
  }
}
