package com.doan1.marketplace.service;

import com.doan1.marketplace.dto.response.ReviewEligibility;
import com.doan1.marketplace.entity.ProviderReview;
import com.doan1.marketplace.client.ProfileRatingClient;
import com.doan1.marketplace.repository.ProviderReviewRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ProviderReviewService {
    private final ProviderReviewRepository reviews;
    private final ProfileRatingClient profiles;

    public ProviderReviewService(ProviderReviewRepository reviews, ProfileRatingClient profiles) {
        this.reviews = reviews;
        this.profiles = profiles;
    }

    @Transactional
    public ProviderReview addReview(
            UUID bookingId,
            UUID reviewerAccountId,
            String reviewerRole,
            ReviewEligibility eligibility,
            int rating,
            String comment) {
        if (reviews.existsByBookingIdAndReviewerAccountId(bookingId, reviewerAccountId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bạn đã gửi đánh giá cho booking này.");
        }
        ProviderReview review = reviews.save(new ProviderReview(
                bookingId,
                reviewerAccountId,
                reviewerRole,
                eligibility.targetId(),
                eligibility.targetRole(),
                rating,
                comment == null ? "" : comment.trim()));
        reviews.flush();
        if ("PROVIDER".equals(eligibility.targetRole())) {
            profiles.addReviewRating(review.getId(), eligibility.targetId(), rating);
        }
        return review;
    }
}
