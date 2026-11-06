CREATE TABLE notifications (
    id BINARY(16) NOT NULL PRIMARY KEY,
    event_id BINARY(16) NOT NULL UNIQUE,
    recipient_id BINARY(16) NULL,
    recipient_role VARCHAR(16) NOT NULL,
    event_type VARCHAR(40) NOT NULL,
    package_id BINARY(16) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    INDEX idx_notifications_recipient_created (recipient_id, created_at),
    INDEX idx_notifications_role_created (recipient_role, created_at)
);
