CREATE TABLE customer_profiles (
    account_id BINARY(16) NOT NULL PRIMARY KEY,
    display_name VARCHAR(120) NOT NULL,
    phone_number VARCHAR(24) NOT NULL,
    default_address VARCHAR(500) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL
);
