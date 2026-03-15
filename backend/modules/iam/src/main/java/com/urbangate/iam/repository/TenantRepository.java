// Copyright (c) UrbanGate
package com.urbangate.iam.repository;

import com.urbangate.iam.domain.entity.TenantEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<TenantEntity, UUID> {
  Optional<TenantEntity> findBySlug(String slug);
}
