// Copyright (c) UrbanGate
package com.urbangate.iam.repository.impl;

import com.urbangate.iam.entity.TenantConfiguration;
import com.urbangate.iam.repository.TenantConfigurationRepository;
import com.urbangate.shared.enums.EntityStatus;
import java.sql.*;
import java.util.ArrayList;
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
public class TenantConfigurationRepositoryImpl implements TenantConfigurationRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  @Override
  public TenantConfiguration insert(TenantConfiguration config) {
    config.prepareForInsert();
    String sql =
        """
            INSERT INTO tenant_configurations(
                name,
                realm,
                description,
                icon,
                creator,
                address,
                state,
                phone,
                number_of_days_before_overdue,
                number_of_days_before_upcoming_payment,
                estate_code,
                send_birthday_shout,
                maximum_guests_for_multiple_code,
                payable_bills,
                created_on,
                last_modified_on,
                entity_status
            ) VALUES (
                       :name,
                      :realm,
                :description,
                :icon,
                :creator,
                :address,
                :state,
                :phone,
                :number_of_days_before_overdue,
                :number_of_days_before_upcoming_payment,
                :estate_code,
                :send_birthday_shout,
                :maximum_guests_for_multiple_code,
                :payable_bills,
                :created_on,
                :last_modified_on,
                :entity_status
            ) RETURNING *
            """;

    MapSqlParameterSource params =
        new MapSqlParameterSource()
            .addValue("name", config.getName())
            .addValue("realm", config.getRealm())
            .addValue("description", config.getDescription())
            .addValue("icon", config.getIcon())
            .addValue("creator", config.getCreator())
            .addValue("address", config.getAddress())
            .addValue("state", config.getState())
            .addValue("phone", config.getPhone())
            .addValue("number_of_days_before_overdue", config.getNumberOfDaysBeforeOverdue())
            .addValue(
                "number_of_days_before_upcoming_payment",
                config.getNumberOfDaysBeforeUpcomingPayment())
            .addValue("estate_code", config.getEstateCode())
            .addValue("send_birthday_shout", config.isSendBirthdayShout())
            .addValue("maximum_guests_for_multiple_code", config.getMaximumGuestsForMultipleCode())
            .addValue(
                "payable_bills",
                config.getPayableBills() != null
                    ? config.getPayableBills().stream()
                        .map(Enum::name) // convert PayableBills -> String
                        .toArray(String[]::new)
                    : new String[0],
                Types.ARRAY)
            .addValue("created_on", config.getCreatedOn())
            .addValue("last_modified_on", config.getLastModifiedOn())
            .addValue(
                "entity_status",
                config.getEntityStatus() != null
                    ? config.getEntityStatus().name()
                    : EntityStatus.ACTIVE.name());

    try {
      return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> mapToEstateConfiguration(rs));
    } catch (Exception e) {
      log.error("Error inserting estate configuration", e);
      throw new RuntimeException("Failed to insert estate configuration", e);
    }
  }

  private static TenantConfiguration mapToEstateConfiguration(ResultSet rs) throws SQLException {
    TenantConfiguration configuration = new TenantConfiguration();
    configuration.setId(rs.getLong("id"));
    configuration.setName(rs.getString("name"));
    configuration.setDescription(rs.getString("description"));
    configuration.setIcon(rs.getString("icon"));
    configuration.setCreator(rs.getString("creator"));
    configuration.setAddress(rs.getString("address"));
    configuration.setState(rs.getString("state"));
    configuration.setPhone(rs.getString("phone"));
    configuration.setRealm(rs.getString("realm"));
    configuration.setNumberOfDaysBeforeOverdue(rs.getInt("number_of_days_before_overdue"));
    configuration.setNumberOfDaysBeforeUpcomingPayment(
        rs.getInt("number_of_days_before_upcoming_payment"));
    configuration.setEstateCode(rs.getString("estate_code"));
    configuration.setSendBirthdayShout(rs.getBoolean("send_birthday_shout"));
    configuration.setMaximumGuestsForMultipleCode(rs.getInt("maximum_guests_for_multiple_code"));

    // Handle array
    Array billsArray = rs.getArray("payable_bills");
    if (billsArray != null) {
      String[] bills = (String[]) billsArray.getArray();
      //            configuration.setPayableBills( Arrays.asList( bills));
    } else {
      configuration.setPayableBills(new ArrayList<>());
    }

    configuration.setCreatedOn(rs.getTimestamp("created_on"));
    configuration.setLastModifiedOn(rs.getTimestamp("last_modified_on"));
    configuration.setEntityStatus(EntityStatus.valueOf(rs.getString("entity_status")));

    return configuration;
  }

  @Override
  public TenantConfiguration update(TenantConfiguration entity) {
    return null;
  }

  public TenantConfiguration update(Long id, TenantConfiguration config) {

    StringBuilder sql =
        new StringBuilder("UPDATE tenant_configurations SET last_modified_on = CURRENT_TIMESTAMP");
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("id", id);

    if (config.getNumberOfDaysBeforeOverdue() > 0) {
      sql.append(", number_of_days_before_overdue = :numberOfDaysBeforeOverdue");
      params.addValue("numberOfDaysBeforeOverdue", config.getNumberOfDaysBeforeOverdue());
    }

    if (config.getNumberOfDaysBeforeUpcomingPayment() > 0) {
      sql.append(", number_of_days_before_upcoming_payment = :numberOfDaysBeforeUpcomingPayment");
      params.addValue(
          "numberOfDaysBeforeUpcomingPayment", config.getNumberOfDaysBeforeUpcomingPayment());
    }

    if (config.getEstateCode() != null && !config.getEstateCode().isBlank()) {
      sql.append(", estate_code = :estateCode");
      params.addValue("estateCode", config.getEstateCode());
    }

    sql.append(", send_birthday_shout = :sendBirthdayShout");
    params.addValue("sendBirthdayShout", config.isSendBirthdayShout());

    if (config.getMaximumGuestsForMultipleCode() >= 0) {
      sql.append(", maximum_guests_for_multiple_code = :maximumGuestsForMultipleCode");
      params.addValue("maximumGuestsForMultipleCode", config.getMaximumGuestsForMultipleCode());
    }

    if (config.getPayableBills() != null) {
      sql.append(", payable_bills = CAST(:payableBills AS text[])");
      params.addValue("payableBills", config.getPayableBills().toArray(new String[0]));
    }

    sql.append(" WHERE id = :id RETURNING *");

    try {
      return jdbcTemplate.queryForObject(
          sql.toString(), params, (rs, rowNum) -> mapToEstateConfiguration(rs));
    } catch (EmptyResultDataAccessException e) {
      log.error("Estate configuration with id {} not found", id);
      throw new RuntimeException("Estate configuration not found");
    } catch (Exception e) {
      log.error("Error updating estate configuration", e);
      throw new RuntimeException("Failed to update estate configuration", e);
    }
  }

  @Override
  public Optional<TenantConfiguration> findById(Long id) {
    String sql =
        """
                SELECT * FROM tenant_configurations
                WHERE id = :id
                """;

    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);

    try {
      TenantConfiguration estate =
          jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> mapToEstateConfiguration(rs));
      return Optional.ofNullable(estate);
    } catch (EmptyResultDataAccessException e) {
      log.debug("No estate config found with id: {}", id);
      return Optional.empty();
    }
  }

  @Override
  public Optional<TenantConfiguration> findByRealm(String realm) {
    String sql =
        """
                SELECT * FROM tenant_configurations
                WHERE realm = :realm
                """;

    MapSqlParameterSource params = new MapSqlParameterSource().addValue("realm", realm);

    try {
      TenantConfiguration estate =
          jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> mapToEstateConfiguration(rs));
      return Optional.ofNullable(estate);
    } catch (EmptyResultDataAccessException e) {
      log.debug("No estate config found with realm: {}", realm);
      return Optional.empty();
    }
  }

  @Override
  public List<TenantConfiguration> findAll() {
    return List.of();
  }
}
