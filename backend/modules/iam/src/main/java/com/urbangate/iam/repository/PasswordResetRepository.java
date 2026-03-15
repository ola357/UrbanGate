// Copyright (c) UrbanGate
package com.urbangate.iam.repository;

import com.urbangate.iam.domain.entity.PasswordReset;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, UUID> {
  Optional<PasswordReset>
      findFirstByResidentIdAndConsumedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(
          UUID residentId, Instant now);

  Optional<PasswordReset> findByResidentIdAndCodeAndConsumedAtIsNull(UUID residentId, String code);
}
