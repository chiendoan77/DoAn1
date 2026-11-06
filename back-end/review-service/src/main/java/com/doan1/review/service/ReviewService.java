package com.doan1.review.service;

import com.doan1.review.client.BookingReviewEligibilityClient;
import com.doan1.review.dto.ReviewEligibility;
import com.doan1.review.dto.ReviewResponse;
import com.doan1.review.entity.Review;
import com.doan1.review.entity.ReviewOutboxEvent;
import com.doan1.review.repository.ReviewOutboxEventRepository;
import com.doan1.review.repository.ReviewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {
    private final ReviewRepository reviews;
    private final ReviewOutboxEventRepository outbox;
    private final BookingReviewEligibilityClient booking;
    private final ObjectMapper objectMapper;

    public ReviewService(ReviewRepository reviews, ReviewOutboxEventRepository outbox,
            BookingReviewEligibilityClient booking, ObjectMapper objectMapper) {
        this.reviews = reviews;
        this.outbox = outbox;
        this.booking = booking;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ReviewResponse add(UUID reviewerId, String reviewerRole, UUID bookingId, int rating, String comment) {
        if (!"CUSTOMER".equals(reviewerRole) && !"PROVIDER".equals(reviewerRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vai trò này không thể gửi đánh giá.");
        }
        if (reviews.existsByBookingIdAndReviewerAccountId(bookingId, reviewerId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bạn đã gửi đánh giá cho booking này.");
        }
        ReviewEligibility eligibility = booking.check(bookingId, reviewerId, reviewerRole);
        Review review = reviews.save(new Review(null, bookingId, reviewerId, reviewerRole,
                eligibility.targetId(), eligibility.targetRole(), rating,
                comment == null ? "" : comment.trim(), Instant.now()));
        try {
            UUID eventId = UUID.randomUUID();
            outbox.save(new ReviewOutboxEvent(eventId, review.getId(), "ReviewSubmitted",
                    objectMapper.writeValueAsString(new ReviewSubmittedEvent(
                            eventId, "ReviewSubmitted", review.getId(), review.getTargetId(),
                            review.getTargetRole(), review.getRating(), review.getCreatedAt()))));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize ReviewSubmitted event.", exception);
        }
        return response(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> mine(UUID accountId) {
        return reviews.findByReviewerAccountIdOrderByCreatedAtDesc(accountId)
                .stream().map(ReviewService::response).toList();
    }

    @Transactional
    public int importLegacy(List<ReviewResponse> legacyReviews) {
        int imported = 0;
        for (ReviewResponse source : legacyReviews) {
            if (reviews.existsById(source.id())) continue;
            reviews.save(new Review(source.id(), source.bookingId(), source.reviewerAccountId(),
                    source.reviewerRole(), source.targetId(), source.targetRole(), source.rating(),
                    source.comment(), source.createdAt()));
            imported++;
        }
        return imported;
    }

    private static ReviewResponse response(Review review) {
        return new ReviewResponse(review.getId(), review.getBookingId(), review.getReviewerAccountId(),
                review.getReviewerRole(), review.getTargetId(), review.getTargetRole(),
                review.getRating(), review.getComment(), review.getCreatedAt());
    }

    private record ReviewSubmittedEvent(
            UUID eventId, String eventType, UUID reviewId, UUID targetId, String targetRole, int rating, Instant occurredAt) {
    }
}
