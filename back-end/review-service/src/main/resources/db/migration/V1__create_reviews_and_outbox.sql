CREATE TABLE reviews (
    id BINARY(16) NOT NULL PRIMARY KEY,
    booking_id BINARY(16) NOT NULL,
    reviewer_account_id BINARY(16) NOT NULL,
    reviewer_role VARCHAR(16) NOT NULL,
    target_id BINARY(16) NOT NULL,
    target_role VARCHAR(16) NOT NULL,
    rating INT NOT NULL,
    comment VARCHAR(1000) NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_review_booking_reviewer UNIQUE (booking_id, reviewer_account_id)
);
CREATE INDEX idx_reviews_reviewer_created ON reviews (reviewer_account_id, created_at);

CREATE TABLE review_outbox_events (
    id BINARY(16) NOT NULL PRIMARY KEY,
    aggregate_id BINARY(16) NOT NULL,
    event_type VARCHAR(48) NOT NULL,
    payload VARCHAR(10000) NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    published_at TIMESTAMP(6) NULL
);
CREATE INDEX idx_review_outbox_status_created ON review_outbox_events (status, created_at);
