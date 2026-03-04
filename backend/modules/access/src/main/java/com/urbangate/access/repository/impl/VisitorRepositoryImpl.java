// Copyright (c) UrbanGate
package com.urbangate.access.repository.impl;

import com.urbangate.access.entity.Visitor;
import com.urbangate.access.repository.VisitorRepository;
import com.urbangate.shared.enums.EntityStatus;
import com.urbangate.shared.enums.ExceptionResponse;
import com.urbangate.shared.exceptions.DataBaseOperationException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class VisitorRepositoryImpl implements VisitorRepository {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public Visitor insert(Visitor entity) {
    entity.prepareForInsert();
    String sql =
        """
                    INSERT INTO visitors(
                        access_code,
                        name,
                        phone,
                        email,
                        visitor_type,
                        created_on,
                        last_modified_on,
                        entity_status
                    ) VALUES (
                        :access_code,
                        :name,
                        :phone,
                        :email,
                        :visitor_type,
                        :created_on,
                        :last_modified_on,
                        :entity_status
                    ) RETURNING *
                    """;

    MapSqlParameterSource params =
        new MapSqlParameterSource()
            .addValue("access_code", entity.getAccessCode())
            .addValue("name", entity.getName())
            .addValue("phone", entity.getPhone())
            .addValue("email", entity.getEmail())
            .addValue("visitor_type", entity.getVisitorType())
            .addValue("created_on", entity.getCreatedOn())
            .addValue("last_modified_on", entity.getLastModifiedOn())
            .addValue("entity_status", entity.getEntityStatus().name());
    try {
      return namedParameterJdbcTemplate.queryForObject(sql, params, this::mapRow);
    } catch (Exception e) {
      log.error("Error visitor code", e);
      throw new DataBaseOperationException(ExceptionResponse.UNABLE_TO_INSERT_RECORD);
    }
  }

  @Override
  public Visitor update(Visitor entity) {
    return null;
  }

  @Override
  public Optional<Visitor> findById(Long key) {
    return Optional.empty();
  }

  @Override
  public Optional<Visitor> findByCode(String code) {
    return Optional.empty();
  }

  @Override
  public List<Visitor> findAll() {
    return List.of();
  }

  @Override
  public Optional<Visitor> findByAccessCode(String code) {
    String sql =
        """
                    SELECT * FROM visitors where access_code = :code
                    """;

    try {
      MapSqlParameterSource params = new MapSqlParameterSource().addValue("code", code);
      return Optional.ofNullable(
          namedParameterJdbcTemplate.queryForObject(sql, params, this::mapRow));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  private Visitor mapRow(ResultSet rs, int rowNum) throws SQLException {
    Visitor visitor = new Visitor();
    visitor.setName(rs.getString("name"));
    visitor.setEmail(rs.getString("email"));
    visitor.setAccessCode(rs.getString("access_code"));
    visitor.setPhone(rs.getString("phone"));
    visitor.setVisitorType(rs.getString("visitor_type"));

    // BaseEntity fields
    visitor.setCreatedOn(rs.getTimestamp("created_on"));
    visitor.setLastModifiedOn(rs.getTimestamp("last_modified_on"));
    visitor.setEntityStatus(EntityStatus.valueOf(rs.getString("entity_status")));

    return visitor;
  }
}
