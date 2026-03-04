CREATE TABLE IF NOT EXISTS visitors (
    name  TEXT NOT NULL,
    access_code  TEXT NOT NULL,
    phone TEXT,
    email TEXT,
    visitor_type TEXT,



    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    entity_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE'

);


CREATE INDEX idx_visitors_access_code ON visitors(access_code);
