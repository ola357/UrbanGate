DROP TABLE IF EXISTS estate_configurations CASCADE;
CREATE TABLE IF NOT EXISTS estate_configurations (

    id BIGSERIAL PRIMARY KEY,
    estate_id BIGINT NOT NULL,
    number_of_days_before_overdue INT NOT NULL DEFAULT 30,
    number_of_days_before_upcoming_payment INT NOT NULL DEFAULT 7,
    estate_code TEXT NOT NULL,
    send_birthday_shout BOOLEAN NOT NULL DEFAULT false,
    maximum_guests_for_multiple_code INT NOT NULL DEFAULT 5,
    payable_bills TEXT[],


    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    entity_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',

    CONSTRAINT uk_estate_configurations_estate_id UNIQUE (estate_id),
    CONSTRAINT uk_estate_configurations_estate_code UNIQUE (estate_code),
    CONSTRAINT fk_estate_configurations_estate
    FOREIGN KEY (estate_id)
    REFERENCES estates(id)
    ON DELETE CASCADE,

    CONSTRAINT chk_number_of_days_before_overdue_positive
    CHECK (number_of_days_before_overdue > 0),
    CONSTRAINT chk_number_of_days_before_upcoming_payment_positive
    CHECK (number_of_days_before_upcoming_payment > 0),
    CONSTRAINT chk_maximum_guests_positive
    CHECK (maximum_guests_for_multiple_code >= 0)
    );


CREATE INDEX idx_estate_configurations_estate_id ON estate_configurations(estate_id);
CREATE INDEX idx_estate_configurations_created_on ON estate_configurations(created_on);





