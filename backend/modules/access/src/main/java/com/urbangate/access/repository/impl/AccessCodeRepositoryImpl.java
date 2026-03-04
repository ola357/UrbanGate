// Copyright (c) UrbanGate
package com.urbangate.access.repository.impl;

import com.urbangate.access.entity.AccessCode;
import com.urbangate.access.enums.AccessType;
import com.urbangate.access.repository.AccessCodeRepository;
import com.urbangate.shared.enums.EntityStatus;
import com.urbangate.shared.enums.ExceptionResponse;
import com.urbangate.shared.exceptions.DataBaseOperationException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessCodeRepositoryImpl implements AccessCodeRepository {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private static final String REALM_KEY = "realm";
  private static final String ENTITY_STATUS_KEY = "entity_status";

  @Override
  public Optional<AccessCode> findByCode(String code) {
    String sql =
        """
                        SELECT * FROM access_codes where code = :code
                        """;

    try {
      MapSqlParameterSource params = new MapSqlParameterSource().addValue("code", code);
      return Optional.ofNullable(
          namedParameterJdbcTemplate.queryForObject(sql, params, this::mapRow));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public AccessCode insert(AccessCode entity) {

    entity.prepareForInsert();
    String sql =
        """
                    INSERT INTO access_codes(
                        code,
                        access_type,
                        description,
                        active,
                        expire_time,
                        user_id,
                        purpose_of_visit,
                        no_of_guests,
                        start_time,
                        group_name,
                        realm,
                        created_on,
                        last_modified_on,
                        entity_status
                    ) VALUES (
                        :code,
                        :access_type,
                        :description,
                        :active,
                        :expire_time,
                        :user_id,
                        :purpose_of_visit,
                        :no_of_guests,
                        :start_time,
                        :group_name,
                              :realm,
                        :created_on,
                        :last_modified_on,
                        :entity_status
                    ) RETURNING *
                    """;

    MapSqlParameterSource params =
        new MapSqlParameterSource()
            .addValue("code", entity.getCode())
            .addValue("access_type", entity.getAccessType().name())
            .addValue("description", entity.getDescription())
            .addValue("active", entity.isActive())
            .addValue("expire_time", entity.getExpiryTime())
            .addValue("user_id", entity.getUserId())
            .addValue("purpose_of_visit", entity.getPurposeOfVisit())
            .addValue("no_of_guests", entity.getNoOfGuests())
            .addValue("start_time", entity.getStartTime())
            .addValue("group_name", entity.getGroupName())
            .addValue(REALM_KEY, entity.getRealm())
            .addValue("created_on", entity.getCreatedOn())
            .addValue("last_modified_on", entity.getLastModifiedOn())
            .addValue(ENTITY_STATUS_KEY, entity.getEntityStatus().name());
    try {
      return namedParameterJdbcTemplate.queryForObject(sql, params, this::mapRow);
    } catch (Exception e) {
      log.error("Error inserting access code", e);
      throw new DataBaseOperationException(ExceptionResponse.UNABLE_TO_INSERT_RECORD);
    }
  }

  @Override
  public AccessCode update(AccessCode entity) {
    return null;
  }

  @Override
  public boolean updateExpiryTime(String code, Timestamp expiryTime) {

    String sql =
        """
                    UPDATE access_codes
                    SET expire_time = :expiry_time,
                        last_modified_on = CURRENT_TIMESTAMP
                    WHERE code = :code

                    """;

    try {
      MapSqlParameterSource params =
          new MapSqlParameterSource().addValue("code", code).addValue("expiry_time", expiryTime);

      int updated = namedParameterJdbcTemplate.update(sql, params);
      return updated > 0;
    } catch (EmptyResultDataAccessException e) {
      return false;
    }
  }

  @Override
  public Optional<AccessCode> findById(Long aLong) {
    return Optional.empty();
  }

  @Override
  public List<AccessCode> findAll() {
    return List.of();
  }

  @Override
  public List<AccessCode> findAllByUser(String userId) {
    String sql =
        """
                    SELECT * FROM access_codes where user_id = :userId
                    """;

    try {
      MapSqlParameterSource params = new MapSqlParameterSource().addValue("userId", userId);
      return namedParameterJdbcTemplate.query(sql, params, this::mapRow);
    } catch (EmptyResultDataAccessException e) {
      return Collections.emptyList();
    }
  }

  @Override
  public List<AccessCode> findAllByRealm(String realm) {
    String sql =
        """
                    SELECT * FROM access_codes where realm = :realm
                    """;

    try {
      MapSqlParameterSource params = new MapSqlParameterSource().addValue(REALM_KEY, realm);
      return namedParameterJdbcTemplate.query(sql, params, this::mapRow);
    } catch (EmptyResultDataAccessException e) {
      return Collections.emptyList();
    }
  }

  @Override
  public boolean revokeCode(String code) {
    String sql =
        """
                    UPDATE access_codes
                    SET active = FALSE,
                        last_modified_on = CURRENT_TIMESTAMP
                    WHERE code = :code
                    AND active = TRUE
                    """;

    try {
      MapSqlParameterSource params = new MapSqlParameterSource().addValue("code", code);

      int updated = namedParameterJdbcTemplate.update(sql, params);
      return updated > 0;
    } catch (EmptyResultDataAccessException e) {
      return false;
    }
  }

  private AccessCode mapRow(ResultSet rs, int rowNum) throws SQLException {
    AccessCode code = new AccessCode();
    code.setId(rs.getLong("id"));
    code.setCode(rs.getString("code"));
    code.setAccessType(AccessType.valueOf(rs.getString("access_type")));
    code.setDescription(rs.getString("description"));
    code.setUserId(rs.getString("user_id"));
    code.setPurposeOfVisit(rs.getString("purpose_of_visit"));
    code.setNoOfGuests(rs.getInt("no_of_guests"));
    code.setStartTime(rs.getTimestamp("start_time"));
    code.setGroupName(rs.getString("group_name"));
    code.setActive(rs.getBoolean("active"));
    code.setExpiryTime(rs.getTimestamp("expire_time"));
    code.setRealm(rs.getString(REALM_KEY));
    code.setEntityStatus(EntityStatus.valueOf(rs.getString(ENTITY_STATUS_KEY)));

    // BaseEntity fields
    code.setCreatedOn(rs.getTimestamp("created_on"));
    code.setLastModifiedOn(rs.getTimestamp("last_modified_on"));
    code.setEntityStatus(EntityStatus.valueOf(rs.getString(ENTITY_STATUS_KEY)));

    return code;
  }
}
