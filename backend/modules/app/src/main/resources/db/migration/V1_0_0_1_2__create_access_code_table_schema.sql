CREATE TABLE IF NOT EXISTS access_codes (
                                            id BIGSERIAL PRIMARY KEY,
                                            code TEXT NOT NULL,
                                            access_type TEXT NOT NULL,
                                            description TEXT,
                                            active BOOLEAN NOT NULL DEFAULT true,
                                            expire_time TIMESTAMP ,
                                            user_id TEXT NOT NULL,
                                            purpose_of_visit TEXT NOT NULL,
                                            no_of_guests BIGINT NOT NULL,
                                            start_time TIMESTAMP NOT NULL,
                                            group_name TEXT,
                                            realm TEXT NOT NULL,

                                            created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            last_modified_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            entity_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE'
    );

CREATE INDEX idx_access_code_code ON access_codes(code);
CREATE INDEX idx_access_code_user_id ON access_codes(user_id);