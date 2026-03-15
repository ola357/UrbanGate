// Copyright (c) UrbanGate
package com.urbangate.iam.repository;

import com.urbangate.iam.domain.entity.TenantMembershipEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantMembershipRepository extends JpaRepository<TenantMembershipEntity, UUID> {
  List<TenantMembershipEntity> findAllByUserId(UUID userId);

  Optional<TenantMembershipEntity> findByTenantIdAndUserId(UUID tenantId, UUID userId);
}
