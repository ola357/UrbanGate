// Copyright (c) UrbanGate
package com.urbangate.iam.service;

import com.urbangate.iam.config.LockoutProperties;
import com.urbangate.iam.domain.entity.LoginAttempt;
import com.urbangate.iam.domain.entity.Resident;
import com.urbangate.iam.repository.LoginAttemptRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginAttemptService {
  private final LoginAttemptRepository loginAttemptRepository;
  private final LockoutProperties lockoutProperties;

  public LoginAttemptService(
      LoginAttemptRepository loginAttemptRepository, LockoutProperties lockoutProperties) {
    this.loginAttemptRepository = loginAttemptRepository;
    this.lockoutProperties = lockoutProperties;
  }

  @Transactional
  public void recordAttempt(
      Resident resident,
      boolean success,
      String failureReason,
      String ipAddress,
      String userAgent) {
    LoginAttempt attempt = new LoginAttempt();
    attempt.setResident(resident);
    attempt.setSuccess(success);
    attempt.setFailureReason(failureReason);
    attempt.setIpAddress(ipAddress);
    attempt.setUserAgent(userAgent);
    loginAttemptRepository.save(attempt);
  }

  public boolean isLocked(UUID residentId) {
    Instant since = Instant.now().minus(lockoutProperties.getWindow());
    long failures =
        loginAttemptRepository.countByResidentIdAndSuccessIsFalseAndAttemptedAtAfter(
            residentId, since);
    return failures >= lockoutProperties.getMaxFailures();
  }

  public void ensureNotLocked(UUID residentId) {
    if (isLocked(residentId)) {
      throw new IllegalStateException("Resident account is locked");
    }
  }

  @Transactional
  public void clearFailures(UUID residentId) {
    loginAttemptRepository.deleteByResidentIdAndSuccessIsFalse(residentId);
  }
}
