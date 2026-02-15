DROP TABLE IF EXISTS resident_profiles;
CREATE TABLE IF NOT EXISTS resident_profiles (

    id BIGSERIAL PRIMARY KEY,
    email TEXT NOT NULL,
    user_id TEXT NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    date_of_birth TEXT,
    unit_address TEXT NOT NULL,
    gender TEXT,



    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    entity_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE'

--     CONSTRAINT fk_resident_profiles
--     FOREIGN KEY (user_id)
--     REFERENCES users(id)
--     ON DELETE CASCADE


    );


CREATE INDEX idx_estate_resident_profiles_user_id ON resident_profiles(user_id);






