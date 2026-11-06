CREATE TABLE account_status_history (
    id BINARY(16) NOT NULL PRIMARY KEY,
    account_id BINARY(16) NOT NULL,
    admin_id BINARY(16) NOT NULL,
    previous_status VARCHAR(16) NOT NULL,
    new_status VARCHAR(16) NOT NULL,
    reason VARCHAR(500) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    INDEX idx_account_status_history_account_created (account_id, created_at)
);
