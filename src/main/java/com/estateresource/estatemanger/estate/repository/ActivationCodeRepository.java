package com.estateresource.estatemanger.estate.repository;

import com.estateresource.estatemanger.estate.entity.ActivationCode;
import com.estateresource.estatemanger.shared.repository.BaseRepository;

import java.util.Optional;

public interface ActivationCodeRepository extends BaseRepository<ActivationCode, Long> {
    Optional<ActivationCode> findByCode(String code);

    boolean revokeCode(String code);

    int revokeExpiredCodes();

    ActivationCode findByToken(String token);
}
