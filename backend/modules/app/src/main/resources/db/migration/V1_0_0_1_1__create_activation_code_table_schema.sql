CREATE TABLE IF NOT EXISTS activation_codes (

    id BIGSERIAL PRIMARY KEY,
    code TEXT NOT NULL,
    ttl_in_hours INT NOT NULL,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    user_id TEXT NOT NULL,


    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    entity_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE'



    );


CREATE INDEX idx_activation_codes_code ON activation_codes(code);






