package com.doan1.marketplace.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "provider_reviews", uniqueConstraints = {
        @UniqueConstraint(name = "uk_provider_review_booking_author", columnNames = {"booking_id", "reviewer_account_id"})
})
public class ProviderReview {
    @Id
    private UUID id;

    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;

    @Column(name = "reviewer_account_id", nullable = false)
    private UUID reviewerAccountId;

    @Column(name = "reviewer_role", nullable = false, length = 16)
    private String reviewerRole;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Column(name = "target_role", nullable = false, length = 16)
    private String targetRole;

    @Column(nullable = false)
    private int rating;

    @Column(length = 1000)
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ProviderReview() {
    }

    public ProviderReview(
            UUID bookingId,
            UUID reviewerAccountId,
            String reviewerRole,
            UUID targetId,
            String targetRole,
            int rating,
            String comment) {
        this.id = UUID.randomUUID();
        this.bookingId = bookingId;
        this.reviewerAccountId = reviewerAccountId;
        this.reviewerRole = reviewerRole;
        this.targetId = targetId;
        this.targetRole = targetRole;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public UUID getReviewerAccountId() {
        return reviewerAccountId;
    }

    public String getReviewerRole() {
        return reviewerRole;
    }

    public UUID getTargetId() {
        return targetId;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
