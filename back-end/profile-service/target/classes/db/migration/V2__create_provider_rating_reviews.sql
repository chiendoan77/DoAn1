CREATE TABLE provider_rating_reviews (
    review_id BINARY(16) NOT NULL PRIMARY KEY,
    provider_id BINARY(16) NOT NULL,
    rating INT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    INDEX idx_provider_rating_reviews_provider (provider_id, created_at)
);
