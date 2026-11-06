CREATE TABLE IF NOT EXISTS bookings (
    id BINARY(16) NOT NULL PRIMARY KEY,
    customer_account_id BINARY(16) NOT NULL,
    provider_profile_id BINARY(16) NOT NULL,
    provider_account_id BINARY(16) NOT NULL,
    customer_name VARCHAR(120) NOT NULL,
    provider_name VARCHAR(120) NOT NULL,
    category_id VARCHAR(32) NOT NULL,
    category_name VARCHAR(80) NOT NULL,
    scheduled_at TIMESTAMP(6) NOT NULL,
    address VARCHAR(500) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL
);
CREATE INDEX idx_bookings_customer_created ON bookings (customer_account_id, created_at);
CREATE INDEX idx_bookings_provider_created ON bookings (provider_account_id, created_at);
