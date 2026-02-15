package com.estateresource.estatemanger.shared.repository;

import com.estateresource.estatemanger.shared.entity.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, String> {

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByPhoneNumber(String phoneNumber);

    int updatePassword(User entity, String newPassword);


    Optional<User> findByEstateAndNumber(Long estateId, String number);
}
