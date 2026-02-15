package com.estateresource.estatemanger.security.repository.impl;

import com.estateresource.estatemanger.security.model.entity.RefreshToken;
import com.estateresource.estatemanger.security.model.entity.Role;
import com.estateresource.estatemanger.security.repository.RefreshTokenRepository;
import com.estateresource.estatemanger.shared.enums.ExceptionResponse;
import com.estateresource.estatemanger.shared.exceptions.DataBaseOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<RefreshToken> findByJti(String jti) {
        return Optional.empty();
    }

    @Override
    public RefreshToken insert(RefreshToken entity) {
        entity.prepareForInsert();

        String sql = """
                INSERT INTO refresh_tokens(
                    jti,
                    username,
                    expires_at,
                    revoked,
                    created_on,
                    last_modified_on,
                    entity_status
                ) VALUES (
                :jti, :username, :expires_at, :revoked, :created_on, :last_modified_on, :entity_status
                ) RETURNING *
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("jti", entity.getJti())
                .addValue("username", entity.getUsername())
                .addValue("expires_at", entity.getExpiresAt())
                .addValue("revoked", entity.isRevoked())
                .addValue("created_on", entity.getCreatedOn())
                .addValue("last_modified_on", entity.getLastModifiedOn())
                .addValue("entity_status", entity.getEntityStatus().name());
        try {
            RefreshToken refreshToken =  jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(RefreshToken.class));
            log.info("Inserted Refresh Token | jti={} ", entity.getJti());
            return refreshToken;
        }catch (EmptyResultDataAccessException e){
            throw new DataBaseOperationException(ExceptionResponse.UNABLE_TO_INSERT_RECORD);
        }
    }

    @Override
    public RefreshToken update(RefreshToken entity) {
        return null;
    }

    @Override
    public Optional<RefreshToken> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public List<RefreshToken> findAll() {
        return List.of();
    }
}
