package com.doan1.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reviews", uniqueConstraints = @UniqueConstraint(
        name = "uk_review_booking_reviewer", columnNames = {"booking_id", "reviewer_account_id"}))
public class Review {
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

    protected Review() {
    }

    public Review(UUID id, UUID bookingId, UUID reviewerAccountId, String reviewerRole,
            UUID targetId, String targetRole, int rating, String comment, Instant createdAt) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.bookingId = bookingId;
        this.reviewerAccountId = reviewerAccountId;
        this.reviewerRole = reviewerRole;
        this.targetId = targetId;
        this.targetRole = targetRole;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt == null ? Instant.now() : createdAt;
    }

    public UUID getId() { return id; }
    public UUID getBookingId() { return bookingId; }
    public UUID getReviewerAccountId() { return reviewerAccountId; }
    public String getReviewerRole() { return reviewerRole; }
    public UUID getTargetId() { return targetId; }
    public String getTargetRole() { return targetRole; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public Instant getCreatedAt() { return createdAt; }
}
