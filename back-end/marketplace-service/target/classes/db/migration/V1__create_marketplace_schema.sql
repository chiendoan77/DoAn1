CREATE TABLE IF NOT EXISTS provider_profiles (
    id BINARY(16) NOT NULL PRIMARY KEY,
    identity_account_id BINARY(16) NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    address VARCHAR(500) NOT NULL,
    image_url VARCHAR(2048) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    status VARCHAR(16) NOT NULL,
    rating DOUBLE NOT NULL,
    review_count INT NOT NULL,
    demo_fixture BOOLEAN NOT NULL,
    created_at TIMESTAMP(6) NOT NULL
);

CREATE TABLE IF NOT EXISTS provider_profile_categories (
    provider_profile_id BINARY(16) NOT NULL,
    category_id VARCHAR(32) NOT NULL,
    PRIMARY KEY (provider_profile_id, category_id)
);
CREATE INDEX idx_provider_profile_category ON provider_profile_categories (category_id);

CREATE TABLE IF NOT EXISTS provider_reviews (
    id BINARY(16) NOT NULL PRIMARY KEY,
    booking_id BINARY(16) NOT NULL,
    reviewer_account_id BINARY(16) NOT NULL,
    reviewer_role VARCHAR(16) NOT NULL,
    target_id BINARY(16) NOT NULL,
    target_role VARCHAR(16) NOT NULL,
    rating INT NOT NULL,
    comment VARCHAR(1000) NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_provider_review_booking_author UNIQUE (booking_id, reviewer_account_id)
);
CREATE INDEX idx_provider_reviews_reviewer_created ON provider_reviews (reviewer_account_id, created_at);
CREATE INDEX idx_provider_reviews_target ON provider_reviews (target_id, target_role);
