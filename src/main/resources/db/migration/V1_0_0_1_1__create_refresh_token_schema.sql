DROP TABLE IF EXISTS refresh_tokens;
CREATE TABLE IF NOT EXISTS refresh_tokens (
                                id BIGSERIAL PRIMARY KEY,
                                jti VARCHAR(255) NOT NULL UNIQUE,
                                username TEXT NOT NULL,
                                expires_at TIMESTAMP NOT NULL,
                                revoked BOOLEAN NOT NULL DEFAULT FALSE,
                                created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                last_modified_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                entity_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',

                                CONSTRAINT uk_refresh_tokens_jti UNIQUE (jti)
);


CREATE INDEX idx_refresh_tokens_jti ON refresh_tokens(jti);

CREATE INDEX idx_refresh_tokens_username ON refresh_tokens(username);
