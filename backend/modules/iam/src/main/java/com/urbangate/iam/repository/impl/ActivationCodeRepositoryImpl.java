// Copyright (c) UrbanGate
package com.urbangate.iam.repository.impl;

import com.urbangate.iam.entity.ActivationCode;
import com.urbangate.iam.repository.ActivationCodeRepository;
import com.urbangate.shared.enums.EntityStatus;
import com.urbangate.shared.enums.ExceptionResponse;
import com.urbangate.shared.exceptions.DataBaseOperationException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivationCodeRepositoryImpl implements ActivationCodeRepository {
  private final NamedParameterJdbcTemplate jdbcTemplate;

  @Override
  public ActivationCode insert(ActivationCode activationCode) {
    activationCode.prepareForInsert();
    String sql =
        """
                INSERT INTO activation_codes(
                    code,
                    ttl_in_hours,
                    is_revoked,
                    user_id
                ) VALUES (
                    :code,
                    :ttlInHours,
                    :isRevoked,
                    :userId
                ) RETURNING *
                """;

    MapSqlParameterSource params =
        new MapSqlParameterSource()
            .addValue("code", activationCode.getCode())
            .addValue("ttlInHours", activationCode.getTtlInHours())
            .addValue("isRevoked", activationCode.isRevoked())
            .addValue("userId", activationCode.getUserId());

    try {
      return jdbcTemplate.queryForObject(sql, params, this::mapRow);
    } catch (Exception e) {
      log.error("Error inserting activation code", e);
      throw new DataBaseOperationException(ExceptionResponse.UNABLE_TO_INSERT_RECORD);
    }
  }

  @Override
  public ActivationCode update(ActivationCode entity) {
    return null;
  }

  @Override
  public Optional<ActivationCode> findById(Long aLong) {
    return Optional.empty();
  }

  @Override
  public List<ActivationCode> findAll() {
    String sql =
        """
                SELECT * FROM activation_codes
                """;

    try {
      return jdbcTemplate.query(sql, this::mapRow);
    } catch (EmptyResultDataAccessException e) {
      return Collections.emptyList();
    }
  }

  @Override
  public Optional<ActivationCode> findByCode(String code) {
    String sql =
        """
                SELECT * FROM activation_codes
                WHERE code = :code
                """;

    MapSqlParameterSource params = new MapSqlParameterSource().addValue("code", code);

    try {
      ActivationCode activationCode = jdbcTemplate.queryForObject(sql, params, this::mapRow);
      return Optional.ofNullable(activationCode);
    } catch (EmptyResultDataAccessException e) {
      log.debug("No activation code found: {}", code);
      return Optional.empty();
    }
  }

  @Override
  public boolean revokeCode(String code) {
    String sql =
        """
                UPDATE activation_codes
                SET is_revoked = TRUE,
                    last_modified_on = CURRENT_TIMESTAMP
                WHERE code = :code
                AND is_revoked = FALSE
                """;

    MapSqlParameterSource params = new MapSqlParameterSource().addValue("code", code);

    int updated = jdbcTemplate.update(sql, params);
    return updated > 0;
  }

  @Override
  public int revokeExpiredCodes() {
    String sql =
        """
                UPDATE activation_codes
                SET is_revoked = TRUE,
                    last_modified_on = CURRENT_TIMESTAMP
                WHERE is_revoked = FALSE
                AND created_on + (ttl_in_hours || ' hours')::interval < CURRENT_TIMESTAMP
                """;

    return jdbcTemplate.update(sql, new MapSqlParameterSource());
  }

  private ActivationCode mapRow(ResultSet rs, int rowNum) throws SQLException {
    ActivationCode code = new ActivationCode();
    code.setId(rs.getLong("id"));
    code.setCode(rs.getString("code"));
    code.setTtlInHours(rs.getInt("ttl_in_hours"));
    code.setRevoked(rs.getBoolean("is_revoked"));
    code.setUserId(rs.getString("user_id"));

    // BaseEntity fields
    code.setCreatedOn(rs.getTimestamp("created_on"));
    code.setLastModifiedOn(rs.getTimestamp("last_modified_on"));
    code.setEntityStatus(EntityStatus.valueOf(rs.getString("entity_status")));

    return code;
  }

  @Override
  public ActivationCode findByToken(String token) {
    return null;
  }
}
