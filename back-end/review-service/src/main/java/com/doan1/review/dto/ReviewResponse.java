package com.doan1.review.dto;

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
}
