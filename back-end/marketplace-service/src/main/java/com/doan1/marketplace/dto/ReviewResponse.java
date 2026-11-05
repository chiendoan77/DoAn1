package com.doan1.marketplace.dto;

import com.doan1.marketplace.model.ProviderReview;

import java.time.Instant;
import java.util.UUID;

public record ReviewResponse(
        UUID id,
        UUID bookingId,
        UUID reviewerAccountId,
        String reviewerRole,
        UUID targetId,
        String targetRole,
        int rating,
        String comment,
        Instant createdAt) {
    public static ReviewResponse from(ProviderReview review) {
        return new ReviewResponse(
                review.getId(),
                review.getBookingId(),
                review.getReviewerAccountId(),
                review.getReviewerRole(),
                review.getTargetId(),
                review.getTargetRole(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt());
    }
}
