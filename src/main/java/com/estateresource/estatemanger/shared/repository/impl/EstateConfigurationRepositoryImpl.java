package com.estateresource.estatemanger.shared.repository.impl;

import com.estateresource.estatemanger.shared.entity.EstateConfiguration;
import com.estateresource.estatemanger.shared.enums.EntityStatus;
import com.estateresource.estatemanger.shared.repository.EstateConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstateConfigurationRepositoryImpl implements EstateConfigurationRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public EstateConfiguration insert(EstateConfiguration config) {
        String sql = """
            INSERT INTO estate_configurations(
                estate_id,
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
                :estate_id,
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

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("estate_id", config.getEstateId())
                .addValue("number_of_days_before_overdue", config.getNumberOfDaysBeforeOverdue())
                .addValue("number_of_days_before_upcoming_payment", config.getNumberOfDaysBeforeUpcomingPayment())
                .addValue("estate_code", config.getEstateCode())
                .addValue("send_birthday_shout", config.isSendBirthdayShout())
                .addValue("maximum_guests_for_multiple_code", config.getMaximumGuestsForMultipleCode())
                .addValue("payable_bills", config.getPayableBills() != null ?
                        config.getPayableBills().toArray(new String[0]) : new String[0], Types.ARRAY)
                .addValue("created_on", new Timestamp(System.currentTimeMillis()))
                .addValue("last_modified_on", new Timestamp(System.currentTimeMillis()))
                .addValue("entity_status", config.getEntityStatus() != null ?
                        config.getEntityStatus().name() : EntityStatus.ACTIVE.name());

        try {
            return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> mapToEstateConfiguration(rs));
        } catch (Exception e) {
            log.error("Error inserting estate configuration", e);
            throw new RuntimeException("Failed to insert estate configuration", e);
        }
    }

    private static EstateConfiguration mapToEstateConfiguration(ResultSet rs) throws SQLException {
        EstateConfiguration configuration = new EstateConfiguration();
        configuration.setId(rs.getLong("id"));
        configuration.setEstateId(String.valueOf(rs.getLong("estate_id")));
        configuration.setNumberOfDaysBeforeOverdue(rs.getInt("number_of_days_before_overdue"));
        configuration.setNumberOfDaysBeforeUpcomingPayment(rs.getInt("number_of_days_before_upcoming_payment"));
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
    public EstateConfiguration update(EstateConfiguration entity) {
        return null;
    }

    public EstateConfiguration update(Long id, EstateConfiguration config) {

        StringBuilder sql = new StringBuilder("UPDATE estate_configurations SET last_modified_on = CURRENT_TIMESTAMP");
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);


        if (config.getEstateId() != null) {
            sql.append(", estate_id = :estateId");
            params.addValue("estateId", config.getEstateId());
        }

        if (config.getNumberOfDaysBeforeOverdue() > 0) {
            sql.append(", number_of_days_before_overdue = :numberOfDaysBeforeOverdue");
            params.addValue("numberOfDaysBeforeOverdue", config.getNumberOfDaysBeforeOverdue());
        }

        if (config.getNumberOfDaysBeforeUpcomingPayment() > 0) {
            sql.append(", number_of_days_before_upcoming_payment = :numberOfDaysBeforeUpcomingPayment");
            params.addValue("numberOfDaysBeforeUpcomingPayment", config.getNumberOfDaysBeforeUpcomingPayment());
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
            return jdbcTemplate.queryForObject(sql.toString(), params, this::mapRow);
        } catch (EmptyResultDataAccessException e) {
            log.error("Estate configuration with id {} not found", id);
            throw new RuntimeException("Estate configuration not found");
        } catch (Exception e) {
            log.error("Error updating estate configuration", e);
            throw new RuntimeException("Failed to update estate configuration", e);
        }
    }

    private EstateConfiguration mapRow(ResultSet rs, int rowNum) throws SQLException {
        EstateConfiguration config = new EstateConfiguration();
        config.setId(rs.getLong("id"));
        config.setEstateId(String.valueOf(rs.getLong("estate_id")));
        config.setNumberOfDaysBeforeOverdue(rs.getInt("number_of_days_before_overdue"));
        config.setNumberOfDaysBeforeUpcomingPayment(rs.getInt("number_of_days_before_upcoming_payment"));
        config.setEstateCode(rs.getString("estate_code"));
        config.setSendBirthdayShout(rs.getBoolean("send_birthday_shout"));
        config.setMaximumGuestsForMultipleCode(rs.getInt("maximum_guests_for_multiple_code"));


        Array billsArray = rs.getArray("payable_bills");
        if (billsArray != null) {
            String[] bills = (String[]) billsArray.getArray();
//            config.setPayableBills(Arrays.asList(bills));
        } else {
//            config.setPayableBills(new ArrayList<>());
        }

        config.setCreatedOn(rs.getTimestamp("created_on"));
        config.setLastModifiedOn(rs.getTimestamp("last_modified_on"));
        config.setEntityStatus(EntityStatus.valueOf(rs.getString("entity_status")));

        return config;
    }


    @Override
    public Optional<EstateConfiguration> findById(Long id) {
        String sql = """
                SELECT * FROM estate_configurations
                WHERE id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        try {
            EstateConfiguration estate = jdbcTemplate.queryForObject(sql, params, this::mapRow);
            return Optional.ofNullable(estate);
        } catch (EmptyResultDataAccessException e) {
            log.debug("No estate config found with id: {}", id);
            return Optional.empty();
        }
    }

    @Override
    public List<EstateConfiguration> findAll() {
        return List.of();
    }
}
