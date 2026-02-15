

DROP TABLE IF EXISTS estates CASCADE;

CREATE TABLE IF NOT EXISTS estates (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    icon VARCHAR(500),
    creator BIGINT NOT NULL,
    address TEXT NOT NULL,
    state VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    configuration_id BIGINT,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    entity_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',

    CONSTRAINT uk_estates_name UNIQUE (name)
    );



CREATE INDEX idx_estates_configuration_id ON estates(configuration_id);
CREATE INDEX idx_estates_name ON estates(name);


