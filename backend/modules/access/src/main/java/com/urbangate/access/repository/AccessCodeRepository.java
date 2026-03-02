// Copyright (c) UrbanGate
package com.urbangate.access.repository;

import com.urbangate.access.entity.AccessCode;
import com.urbangate.shared.repository.BaseRepository;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface AccessCodeRepository extends BaseRepository<AccessCode, Long> {
  Optional<AccessCode> findByCode(String code);

  boolean updateExpiryTime(String code, Timestamp expiryTime);

  List<AccessCode> findAllByUser(String userId);

  List<AccessCode> findAllByRealm(String realm);

  boolean revokeCode(String code);
}
