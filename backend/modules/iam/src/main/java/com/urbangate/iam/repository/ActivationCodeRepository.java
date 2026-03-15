// Copyright (c) UrbanGate
package com.urbangate.iam.repository;

import com.urbangate.iam.domain.entity.ActivationCode;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivationCodeRepository extends JpaRepository<ActivationCode, UUID> {
  Optional<ActivationCode> findByCode(String code);

  Optional<ActivationCode> findByCodeAndEstateId(String code, UUID estateId);

  Optional<ActivationCode> findFirstByResidentIdAndExpiresAtAfterOrderByCreatedAtDesc(
      UUID residentId, Instant now);
}
