package com.estateresource.estatemanger.security.repository.impl;

import com.estateresource.estatemanger.security.model.entity.Role;
import com.estateresource.estatemanger.security.repository.RoleRepository;
import com.estateresource.estatemanger.shared.entity.User;
import com.estateresource.estatemanger.shared.enums.ExceptionResponse;
import com.estateresource.estatemanger.shared.exceptions.DataBaseOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Role insert(Role entity) {

        entity.prepareForInsert();

        String sql = """
                INSERT INTO roles(
                    name,
                    created_on,
                    last_modified_on,
                    status
                ) VALUES (
                :name, :created_on, :last_modified_on, :status
                ) RETURNING *
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", entity.getName().name())
                .addValue("created_on", entity.getCreatedOn())
                .addValue("last_modified_on", entity.getLastModifiedOn())
                .addValue("status", entity.getEntityStatus().name());
        try {
            Role role =  jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Role.class));
            log.info("Inserted Role | name={} ", entity.getName());
            return role;
        }catch (EmptyResultDataAccessException e){
            throw new DataBaseOperationException(ExceptionResponse.UNABLE_TO_INSERT_RECORD);
        }
    }

    @Override
    public Role update(Role entity) {
        return null;
    }

    @Override
    public Optional<Role> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<Role> findAll() {
        return List.of();
    }

    @Override
    public Set<Role> findRolesByUserPhoneNumber(String phoneNumber) {
        String sql = """
            SELECT r.id, r.name, r.status, r.created_on, r.last_modified_on
            FROM user_roles ur
            JOIN roles r ON r.id = ur.role_id
            WHERE ur.user_phone_number = :phoneNumber
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("phoneNumber", phoneNumber);

        List<Role> roles = jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            Role role = new Role();
            role.setId(rs.getLong("id"));
            role.setName(com.estateresource.estatemanger.security.model.enums.Role.valueOf(rs.getString("name")));
            role.setCreatedOn(rs.getTimestamp("created_on"));
            role.setLastModifiedOn(rs.getTimestamp("last_modified_on"));
            return role;
        });

        return new HashSet<>(roles);
    }

    @Override
    public Optional<Role> findRoleByName(String name) {
        String sql = """
                select * from roles where name = :name
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", name);
        try {
            Role role =  jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Role.class));
            log.info("Found Role | name={} ", name);
            return Optional.ofNullable(role);
        }catch (EmptyResultDataAccessException e){
            throw new DataBaseOperationException(ExceptionResponse.UNABLE_TO_FETCH_RECORD);
        }
    }

    @Override
    public int assignRoleToUser(String phoneNumber, Long id) {


        //TODO: USER USER_ID IN PLACE OF EMAIL

        String sql = """
                INSERT INTO user_roles( 
                                    
                    user_phone_number,
                    role_id
                ) VALUES (
                :user_phone_number, :role_id 
                ) 
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_phone_number", phoneNumber)
                .addValue("role_id", id);
        try {
            int query =  jdbcTemplate.update(sql, params);
            log.info("Inserted User Role | name={} | role={} ", phoneNumber, id);
            return query;
        }catch (EmptyResultDataAccessException e){
            throw new DataBaseOperationException(ExceptionResponse.UNABLE_TO_INSERT_RECORD);
        }
    }
}
