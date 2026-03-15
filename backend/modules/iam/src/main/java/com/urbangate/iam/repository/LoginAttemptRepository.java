// Copyright (c) UrbanGate
package com.urbangate.iam.repository;

import com.urbangate.iam.domain.entity.LoginAttempt;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, UUID> {
  long countByResidentIdAndSuccessIsFalseAndAttemptedAtAfter(UUID residentId, Instant since);

  long deleteByResidentIdAndSuccessIsFalse(UUID residentId);
}
