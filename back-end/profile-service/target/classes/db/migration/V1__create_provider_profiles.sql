CREATE TABLE provider_profiles (
    id BINARY(16) NOT NULL PRIMARY KEY,
    identity_account_id BINARY(16) NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    address VARCHAR(500) NOT NULL,
    image_url VARCHAR(2048) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    status VARCHAR(16) NOT NULL,
    suspended BOOLEAN NOT NULL DEFAULT FALSE,
    rating DOUBLE NOT NULL DEFAULT 0,
    review_count INT NOT NULL DEFAULT 0,
    demo_fixture BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP(6) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    INDEX idx_provider_profiles_status_created (status, created_at)
);

CREATE TABLE provider_profile_categories (
    provider_profile_id BINARY(16) NOT NULL,
    category_id VARCHAR(32) NOT NULL,
    PRIMARY KEY (provider_profile_id, category_id),
    CONSTRAINT fk_profile_categories_profile FOREIGN KEY (provider_profile_id)
        REFERENCES provider_profiles (id) ON DELETE CASCADE,
    INDEX idx_provider_profile_categories_category (category_id)
);
