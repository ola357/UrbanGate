// Copyright (c) UrbanGate
package com.urbangate.iam.repository;

import com.urbangate.iam.entity.ActivationCode;
import com.urbangate.shared.repository.BaseRepository;
import java.util.Optional;

public interface ActivationCodeRepository extends BaseRepository<ActivationCode, Long> {
  Optional<ActivationCode> findByCode(String code);

  boolean revokeCode(String code);

  int revokeExpiredCodes();

  ActivationCode findByToken(String token);
}
