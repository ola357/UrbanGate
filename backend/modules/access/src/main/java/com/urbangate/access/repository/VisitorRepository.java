// Copyright (c) UrbanGate
package com.urbangate.access.repository;

import com.urbangate.access.entity.Visitor;
import com.urbangate.shared.repository.BaseRepository;
import java.util.Optional;

public interface VisitorRepository extends BaseRepository<Visitor, Long> {
  Optional<Visitor> findByCode(String code);

  Optional<Visitor> findByAccessCode(String code);
}
