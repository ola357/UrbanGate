// Copyright (c) UrbanGate
package com.urbangate.iam.repository;

import com.urbangate.iam.domain.entity.Estate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstateRepository extends JpaRepository<Estate, UUID> {
  Optional<Estate> findByCode(String code);
}
