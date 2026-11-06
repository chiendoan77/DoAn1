ALTER TABLE bookings
    ADD COLUMN package_id BINARY(16) NULL,
    ADD COLUMN package_name VARCHAR(160) NULL,
    ADD COLUMN package_price DECIMAL(10, 0) NULL,
    ADD COLUMN package_duration_minutes INT NULL;
CREATE INDEX idx_bookings_package ON bookings (package_id);

CREATE TABLE booking_outbox_events (
    id BINARY(16) NOT NULL PRIMARY KEY,
    aggregate_id BINARY(16) NOT NULL,
    event_type VARCHAR(48) NOT NULL,
    payload VARCHAR(10000) NOT NULL,
    exchange_name VARCHAR(100) NOT NULL,
    routing_key VARCHAR(120) NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    published_at TIMESTAMP(6) NULL
);
CREATE INDEX idx_booking_outbox_status_created ON booking_outbox_events (status, created_at);
