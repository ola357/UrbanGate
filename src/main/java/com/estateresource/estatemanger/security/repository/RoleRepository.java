package com.estateresource.estatemanger.security.repository;

import com.estateresource.estatemanger.security.model.entity.Role;
import com.estateresource.estatemanger.shared.repository.BaseRepository;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends BaseRepository<Role, Long> {

    Optional<Role> findRoleByName(String name);
    int assignRoleToUser(String email, Long id);
    public Set<Role> findRolesByUserPhoneNumber(String email);

}
