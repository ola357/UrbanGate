package com.estateresource.estatemanger.shared.repository.impl;

import com.estateresource.estatemanger.security.repository.RoleRepository;
import com.estateresource.estatemanger.shared.entity.User;
import com.estateresource.estatemanger.shared.enums.EntityStatus;
import com.estateresource.estatemanger.shared.enums.ExceptionResponse;
import com.estateresource.estatemanger.shared.exceptions.DataBaseOperationException;
import com.estateresource.estatemanger.shared.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserRepositoryImpl implements UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RoleRepository roleRepository;

    public UserRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate, RoleRepository roleRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.roleRepository = roleRepository;
    }



    @Override
    public Optional<User> findUserByEmail(String email) {
        String sql = "select * from users u " +
                " WHERE u.email = :email ";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email);
        try {
            User user = jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
                User u = new User();

                u.setPhoneNumber(rs.getString("phone_number"));
                u.setEstateId(rs.getLong("estate_id"));
                u.setPassword(rs.getString("password"));
                u.setCreatedOn(rs.getTimestamp("created_on"));
                u.setLastModifiedOn(rs.getTimestamp("last_modified_on"));
                u.setEntityStatus(EntityStatus.valueOf(rs.getString("status")));

                Set<com.estateresource.estatemanger.security.model.entity.Role> roles = roleRepository.findRolesByUserPhoneNumber(email);
                u.setRoles(roles);

                return u;
            });
            return Optional.ofNullable(user);
        }catch (EmptyResultDataAccessException e){
            log.debug("No User Found with email: {}", email);
            return Optional.empty();
        }

    }

    @Override
    public Optional<User> findUserByPhoneNumber(String phoneNumber) {
        String sql = "select * from users u " +
                " WHERE u.phone_number = :phone_number ";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("phone_number", phoneNumber);
        try {
            User user = jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
                User u = new User();
                u.setId(rs.getString("id"));
                u.setPhoneNumber(rs.getString("phone_number"));
                u.setEstateId(rs.getLong("estate_id"));
                u.setPassword(rs.getString("password"));
                u.setCreatedOn(rs.getTimestamp("created_on"));
                u.setLastModifiedOn(rs.getTimestamp("last_modified_on"));
                u.setEntityStatus(EntityStatus.valueOf(rs.getString("status")));

                Set<com.estateresource.estatemanger.security.model.entity.Role> roles = roleRepository.findRolesByUserPhoneNumber(phoneNumber);
                u.setRoles(roles);

                return u;
            });
            return Optional.ofNullable(user);
        }catch (EmptyResultDataAccessException e){
            log.debug("No User Found with email: {}", phoneNumber);
            return Optional.empty();
        }

    }

    @Override
    public User insert(User entity) {
        entity.prepareForInsert();


        String sql = """
                INSERT INTO users(
                                  id,
                    phone_number,
                    estate_id,
                    password,
                    created_on,
                    last_modified_on,
                    status
                ) VALUES ( :id,
                :phone_number, :estate_id, :password, :created_on, :last_modified_on, :status
                ) RETURNING *
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", entity.getId())
                .addValue("phone_number", entity.getPhoneNumber())
                .addValue("estate_id", entity.getEstateId())
                .addValue("password", entity.getPassword())
                .addValue("created_on", entity.getCreatedOn())
                .addValue("last_modified_on", entity.getLastModifiedOn())
                .addValue("status", entity.getEntityStatus().name());
        try {
            User user =  jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(User.class));
            log.info("Inserted User | phoneNumber={} ", entity.getPhoneNumber());
            return user;
        }catch (EmptyResultDataAccessException e){
           throw new DataBaseOperationException(ExceptionResponse.UNABLE_TO_INSERT_RECORD);
        }
    }

    @Override
    public User update(User entity) {
        return null;
    }

    @Override
    public int updatePassword(User entity, String password) {

        String sql = """
                UPDATE users SET password = :password WHERE id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", entity.getId())
                .addValue("password", password);
        try {
            int rowsAffected = jdbcTemplate.update(sql, params);
            return rowsAffected;
        }catch (EmptyResultDataAccessException e){
            log.debug("No Password Updated | User Id={}",  entity.getId());
            return 0;
        }
    }

    @Override
    public Optional<User> findById(String userId) {

        String sql = "select * from users " +
                " WHERE id = :id ";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", userId);
        try {
            User user = jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(User.class));
            return Optional.ofNullable(user);
        }catch (EmptyResultDataAccessException e){
            log.debug("No User Found | User Id={}",  userId);
            return Optional.empty();
        }

    }

    @Override
    public Optional<User> findByEstateAndNumber(Long estateId, String number) {

        String sql = """
        SELECT *
        FROM users
        WHERE estate_id = :estateId
          AND phone_number = :number
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("phone_number", number)
                .addValue("estateId", estateId);
        try {
            User user = jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(User.class));
            return Optional.ofNullable(user);
        }catch (EmptyResultDataAccessException e){
            log.debug("No User Found | phone Number={} | estate Id={}", number, estateId);
            return Optional.empty();
        }

    }

    @Override
    public List<User> findAll() {
        return List.of();
    }
}
