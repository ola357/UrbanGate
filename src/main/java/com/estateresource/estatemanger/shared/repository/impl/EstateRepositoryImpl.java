package com.estateresource.estatemanger.shared.repository.impl;

import com.estateresource.estatemanger.shared.entity.Estate;
import com.estateresource.estatemanger.shared.enums.EntityStatus;
import com.estateresource.estatemanger.shared.repository.EstateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstateRepositoryImpl implements EstateRepository {


    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Estate insert(Estate estate) {
        String sql = """
            INSERT INTO estates(
                name,
                description,
                icon,
                creator,
                address,
                state,
                phone,
                configuration_id,
                created_on,
                last_modified_on,
                entity_status
            ) VALUES (
                :name,
                :description,
                :icon,
                :creator,
                :address,
                :state,
                :phone,
                :configurationId,
                :createdOn,
                :lastModifiedOn,
                :entityStatus
            ) RETURNING *
            """;

        Timestamp now = new Timestamp(System.currentTimeMillis());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", estate.getName())
                .addValue("description", estate.getDescription())
                .addValue("icon", estate.getIcon())
                .addValue("creator", estate.getCreator())
                .addValue("address", estate.getAddress())
                .addValue("state", estate.getState())
                .addValue("phone", estate.getPhone())
                .addValue("configurationId", estate.getConfigurationId())
                .addValue("createdOn", estate.getCreatedOn() != null ? estate.getCreatedOn() : now)
                .addValue("lastModifiedOn", estate.getLastModifiedOn() != null ? estate.getLastModifiedOn() : now)
                .addValue("entityStatus", estate.getEntityStatus() != null ?
                        estate.getEntityStatus().name() : EntityStatus.ACTIVE.name());

        try {
            return jdbcTemplate.queryForObject(sql, params, this::mapRow);
        } catch (Exception e) {
            log.error("Error inserting estate", e);
            throw new RuntimeException("Failed to insert estate", e);
        }
    }


    @Override
    public Estate update(Estate entity) {
        return null;
    }

    @Override
    public Optional<Estate> findById(Long id) {
        String sql = """
                SELECT * FROM estates
                WHERE id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        try {
            Estate estate = jdbcTemplate.queryForObject(sql, params, this::mapRow);
            return Optional.ofNullable(estate);
        } catch (EmptyResultDataAccessException e) {
            log.debug("No estate found with id: {}", id);
            return Optional.empty();
        }
    }


    @Override
    public List<Estate> findAll() {
        return List.of();
    }


    public Optional<Estate> findByName(String name) {
        String sql = """
                SELECT * FROM estates
                WHERE name = :name
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", name);

        try {
            Estate estate = jdbcTemplate.queryForObject(sql, params, this::mapRow);
            return Optional.ofNullable(estate);
        } catch (EmptyResultDataAccessException e) {
            log.debug("No estate found with name: {}", name);
            return Optional.empty();
        }
    }


    private Estate mapRow(ResultSet rs, int rowNum) throws SQLException {
        Estate estate = new Estate();
        estate.setId(rs.getLong("id"));
        estate.setName(rs.getString("name"));
        estate.setDescription(rs.getString("description"));
        estate.setIcon(rs.getString("icon"));
        estate.setCreator(rs.getLong("creator"));
        estate.setAddress(rs.getString("address"));
        estate.setState(rs.getString("state"));
        estate.setPhone(rs.getString("phone"));
        estate.setConfigurationId(rs.getLong("configuration_id"));


        estate.setCreatedOn(rs.getTimestamp("created_on"));
        estate.setLastModifiedOn(rs.getTimestamp("last_modified_on"));
        estate.setEntityStatus(EntityStatus.valueOf(rs.getString("entity_status")));

        return estate;
    }

}
