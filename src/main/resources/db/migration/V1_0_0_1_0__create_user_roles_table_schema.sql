DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;

CREATE TABLE IF NOT EXISTS users (
                                     id TEXT NOT NULL,
                                     phone_number TEXT NOT NULL,
                                     estate_id BIGINT NOT NULL,
                                     password TEXT,
                                     created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     last_modified_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     status TEXT NOT NULL DEFAULT 'ACTIVE',
                                     CONSTRAINT pk_users PRIMARY KEY (phone_number, estate_id),
    CONSTRAINT uk_users_phone_number UNIQUE (phone_number)
    );

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    status TEXT NOT NULL DEFAULT 'ACTIVE',
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS user_roles (
    user_phone_number TEXT NOT NULL,
    role_id BIGINT NOT NULL,

    PRIMARY KEY (user_phone_number, role_id),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_phone_number)
        REFERENCES users (phone_number)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id)
        REFERENCES roles (id)
        ON DELETE CASCADE
);


CREATE INDEX idx_users_phone ON users(phone_number);
CREATE INDEX idx_user_roles_user_phone_number ON user_roles(user_phone_number);
CREATE INDEX idx_user_created_on ON users(created_on);
CREATE INDEX idx_user_last_modified_on ON users(last_modified_on);