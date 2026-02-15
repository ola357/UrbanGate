package com.estateresource.estatemanger.shared.repository.impl;

import com.estateresource.estatemanger.shared.entity.ResidentProfile;
import com.estateresource.estatemanger.shared.enums.EntityStatus;
import com.estateresource.estatemanger.shared.repository.ResidentProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ResidentProfileRepositoryImpl implements ResidentProfileRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;


    public ResidentProfile insert(ResidentProfile profile) {
        String sql = """
    INSERT INTO resident_profiles(
        first_name,
        last_name,  
        date_of_birth,
        email,
        user_id,
        unit_address,
        gender
    ) VALUES (
        :first_name,
        :last_name,
        :dateOfBirth,
        :email,
        :userId,
        :unitAddress,
        :gender
    ) RETURNING *
""";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("first_name", profile.getFirstName())
                .addValue("last_name", profile.getLastName())
                .addValue("email", profile.getEmail())
                .addValue("dateOfBirth", profile.getDateOfBirth())
                .addValue("unitAddress", profile.getUnitAddress())
                .addValue("gender", profile.getGender())
                .addValue("userId", profile.getUserId());

        try {
            return jdbcTemplate.queryForObject(sql, params, this::mapRow);
        } catch (Exception e) {
            log.error("Error inserting resident profile", e);
            throw new RuntimeException("Failed to insert resident profile", e);
        }
    }

    @Override
    public ResidentProfile update(ResidentProfile entity) {
        return null;
    }


    public Optional<ResidentProfile> findById(Long id) {
        String sql = """
                SELECT * FROM resident_profiles
                WHERE id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        try {
            ResidentProfile profile = jdbcTemplate.queryForObject(sql, params, this::mapRow);
            return Optional.ofNullable(profile);
        } catch (EmptyResultDataAccessException e) {
            log.debug("No resident profile found with id: {}", id);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ResidentProfile> findByUserId(String id) {
        String sql = """
                SELECT * FROM resident_profiles
                WHERE user_id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        try {
            ResidentProfile profile = jdbcTemplate.queryForObject(sql, params, this::mapRow);
            return Optional.ofNullable(profile);
        } catch (EmptyResultDataAccessException e) {
            log.debug("No resident profile found with id: {}", id);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ResidentProfile> findByPhoneNumber(String phoneNumber) {
        String sql = """
                SELECT * FROM resident_profiles
                WHERE phone_number = :phoneNumber
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("phoneNumber", phoneNumber);

        try {

            ResidentProfile profile = jdbcTemplate.queryForObject(sql, params, this::mapRow);
            return Optional.ofNullable(profile);

        } catch (EmptyResultDataAccessException e) {

            log.debug("No resident profile found with phone Number: {}", phoneNumber);
            return Optional.empty();

        }
    }

    @Override
    public Optional<ResidentProfile> findByEmailOrPhoneNumber(String email, String phoneNumber) {
        String sql = """
                SELECT * FROM resident_profiles
                WHERE email = :email OR phone_number = :phoneNumber
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email)
                .addValue("phoneNumber", phoneNumber);

        try {

            ResidentProfile profile = jdbcTemplate.queryForObject(sql, params, this::mapRow);
            return Optional.ofNullable(profile);

        } catch (EmptyResultDataAccessException e) {

            log.debug("No resident profile found with email: {}", email);
            return Optional.empty();

        }
    }


    public List<ResidentProfile> findAll() {
        String sql = """
                SELECT * FROM resident_profiles
                WHERE entity_status = 'ACTIVE'
                ORDER BY created_on DESC
                """;

        return jdbcTemplate.query(sql, new MapSqlParameterSource(), this::mapRow);
    }


    public ResidentProfile update(Long id, ResidentProfile profile) {
        List<String> updates = new ArrayList<>();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);


        updates.add("last_modified_on = CURRENT_TIMESTAMP");

        if (profile.getEmail() != null && !profile.getEmail().isBlank()) {
            updates.add("phone_number = :phoneNumber");
            params.addValue("phoneNumber", profile.getEmail());
        }

        if (profile.getEntityStatus() != null) {
            updates.add("entity_status = :entityStatus");
            params.addValue("entityStatus", profile.getEntityStatus().name());
        }

        if (updates.size() == 1) {
            throw new IllegalArgumentException("No fields to update");
        }

        String sql = "UPDATE resident_profiles SET " + String.join(", ", updates) +
                " WHERE id = :id RETURNING *";

        try {
            return jdbcTemplate.queryForObject(sql, params, this::mapRow);
        } catch (EmptyResultDataAccessException e) {
            log.error("Resident profile with id {} not found", id);
            throw new RuntimeException("Resident profile not found");
        } catch (Exception e) {
            log.error("Error updating resident profile", e);
            throw new RuntimeException("Failed to update resident profile", e);
        }
    }



    @Override
    public boolean existsByEmailOrPhoneNumber(String email, String phoneNumber) {
        String sql = """
                SELECT COUNT(*) FROM resident_profiles
                WHERE email = :email OR phone_number = :phoneNumber
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email)
                .addValue("phoneNumber", phoneNumber);

        Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }








    private ResidentProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResidentProfile profile = new ResidentProfile();
        profile.setId(rs.getLong("id"));
        profile.setFirstName(rs.getString("first_name"));
        profile.setLastName(rs.getString("last_name"));
        profile.setDateOfBirth(rs.getString("date_of_birth"));
        profile.setUserId(rs.getString("user_id"));
        profile.setEmail(rs.getString("email"));
        profile.setGender(rs.getString("gender"));
        profile.setUnitAddress(rs.getString("unit_address"));
        profile.setCreatedOn(rs.getTimestamp("created_on"));
        profile.setLastModifiedOn(rs.getTimestamp("last_modified_on"));
        profile.setEntityStatus(EntityStatus.valueOf(rs.getString("entity_status")));

        return profile;
    }
}