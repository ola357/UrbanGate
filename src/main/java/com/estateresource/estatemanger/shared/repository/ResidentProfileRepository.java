package com.estateresource.estatemanger.shared.repository;

import com.estateresource.estatemanger.shared.entity.ResidentProfile;

import java.util.Optional;

public interface ResidentProfileRepository extends BaseRepository<ResidentProfile , Long>{
    Optional<ResidentProfile> findByUserId(String id);

    Optional<ResidentProfile> findByPhoneNumber(String phoneNumber);

    Optional<ResidentProfile> findByEmailOrPhoneNumber(String email, String phoneNumber);

    boolean existsByEmailOrPhoneNumber(String email, String phoneNumber);
}
