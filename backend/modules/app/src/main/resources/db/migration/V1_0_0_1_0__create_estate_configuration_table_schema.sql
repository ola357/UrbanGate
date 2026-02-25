DROP TABLE IF EXISTS tenant_configurations CASCADE;
CREATE TABLE IF NOT EXISTS tenant_configurations (

    id BIGSERIAL PRIMARY KEY,
    number_of_days_before_overdue INT NOT NULL DEFAULT 30,
    number_of_days_before_upcoming_payment INT NOT NULL DEFAULT 7,
    estate_code TEXT NOT NULL,
    realm TEXT NOT NULL,
    send_birthday_shout BOOLEAN NOT NULL DEFAULT false,
    maximum_guests_for_multiple_code INT NOT NULL DEFAULT 5,
    payable_bills TEXT[],


    name VARCHAR(255) NOT NULL,
    description TEXT,
    icon VARCHAR(500),
    creator BIGINT NOT NULL,
    address TEXT NOT NULL,
    state VARCHAR(100) NOT NULL,
    phone VARCHAR(20),

    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    entity_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',



    CONSTRAINT chk_number_of_days_before_overdue_positive
    CHECK (number_of_days_before_overdue > 0),
    CONSTRAINT chk_number_of_days_before_upcoming_payment_positive
    CHECK (number_of_days_before_upcoming_payment > 0),
    CONSTRAINT chk_maximum_guests_positive
    CHECK (maximum_guests_for_multiple_code >= 0)
    );

CREATE INDEX idx_tenant_configurations_created_on ON tenant_configurations(created_on);





