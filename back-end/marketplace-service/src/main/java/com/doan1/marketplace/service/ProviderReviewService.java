package com.doan1.marketplace.service;

import com.doan1.marketplace.dto.response.ReviewEligibility;
import com.doan1.marketplace.entity.ProviderProfile;
import com.doan1.marketplace.entity.ProviderReview;
import com.doan1.marketplace.enums.ProviderStatus;
import com.doan1.marketplace.repository.ProviderProfileRepository;
import com.doan1.marketplace.repository.ProviderReviewRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ProviderReviewService {
    private final ProviderProfileRepository providers;
    private final ProviderReviewRepository reviews;

    public ProviderReviewService(ProviderProfileRepository providers, ProviderReviewRepository reviews) {
        this.providers = providers;
        this.reviews = reviews;
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
        if ("PROVIDER".equals(eligibility.targetRole())) {
            ProviderProfile provider = providers.findByIdForUpdate(eligibility.targetId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Không tìm thấy hồ sơ nhà cung cấp."));
            if (provider.getStatus() != ProviderStatus.APPROVED) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Không thể đánh giá hồ sơ chưa được duyệt.");
            }
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
            Object[] summary = reviews.ratingSummary(eligibility.targetId()).get(0);
            double average = summary[0] == null ? 0 : ((Number) summary[0]).doubleValue();
            int count = ((Number) summary[1]).intValue();
            providers.findByIdForUpdate(eligibility.targetId()).orElseThrow()
                    .setRatingAndReviewCount(average, count);
        }
        return review;
    }
}
