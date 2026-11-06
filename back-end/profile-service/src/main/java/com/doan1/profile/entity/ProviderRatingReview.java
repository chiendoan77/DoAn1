package com.doan1.profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "provider_rating_reviews")
public class ProviderRatingReview {
    @Id
    @Column(name = "review_id")
    private UUID reviewId;
    @Column(name = "provider_id", nullable = false)
    private UUID providerId;
    @Column(nullable = false)
    private int rating;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ProviderRatingReview() {}

    public ProviderRatingReview(UUID reviewId, UUID providerId, int rating) {
        this.reviewId = reviewId;
        this.providerId = providerId;
        this.rating = rating;
        this.createdAt = Instant.now();
    }

    public UUID getReviewId() { return reviewId; }
    public UUID getProviderId() { return providerId; }
    public int getRating() { return rating; }
}
