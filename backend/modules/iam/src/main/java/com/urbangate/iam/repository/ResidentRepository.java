// Copyright (c) UrbanGate
package com.urbangate.iam.repository;

import com.urbangate.iam.domain.entity.Resident;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResidentRepository extends JpaRepository<Resident, UUID> {
  Optional<Resident> findByEstateIdAndPhone(UUID estateId, String phone);

  Optional<Resident> findByEstateIdAndEmail(UUID estateId, String email);

  Optional<Resident> findByEstateIdAndId(UUID estateId, UUID id);
}
