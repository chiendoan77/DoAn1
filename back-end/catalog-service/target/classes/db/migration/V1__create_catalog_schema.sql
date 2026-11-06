CREATE TABLE categories (
    id VARCHAR(32) NOT NULL PRIMARY KEY,
    name VARCHAR(80) NOT NULL UNIQUE,
    description VARCHAR(1000),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL
);

CREATE TABLE service_packages (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    provider_id VARCHAR(36) NOT NULL,
    owner_account_id VARCHAR(36) NOT NULL,
    category_id VARCHAR(32) NOT NULL,
    name VARCHAR(160) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    price DECIMAL(10, 0) NOT NULL,
    duration_minutes INTEGER NOT NULL,
    provider_phone VARCHAR(32) NOT NULL,
    status VARCHAR(24) NOT NULL,
    rejection_reason VARCHAR(1000),
    has_been_submitted BOOLEAN NOT NULL DEFAULT FALSE,
    has_been_rejected BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_service_packages_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE package_history (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    package_id VARCHAR(36) NOT NULL,
    actor_id VARCHAR(36) NOT NULL,
    action VARCHAR(32) NOT NULL,
    reason VARCHAR(1000),
    package_version BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL
);

CREATE TABLE outbox_events (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    aggregate_id VARCHAR(36) NOT NULL,
    event_type VARCHAR(40) NOT NULL,
    payload VARCHAR(10000) NOT NULL,
    exchange_name VARCHAR(100) NOT NULL,
    routing_key VARCHAR(120) NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    published_at TIMESTAMP(6)
);

CREATE INDEX idx_packages_provider ON service_packages (provider_id);
CREATE INDEX idx_packages_owner_account ON service_packages (owner_account_id);
CREATE INDEX idx_packages_category_status ON service_packages (category_id, status);
CREATE INDEX idx_packages_status_created ON service_packages (status, created_at);
CREATE INDEX idx_package_history_package_created ON package_history (package_id, created_at);
CREATE INDEX idx_outbox_status_created ON outbox_events (status, created_at);

INSERT INTO categories (id, name, description, active, created_at, updated_at)
VALUES
    ('cleaning', 'Cleaning', 'Home cleaning services', TRUE, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('repair', 'Repair', 'Home repair services', TRUE, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));
