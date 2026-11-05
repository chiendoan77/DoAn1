package com.doan1.marketplace.mapper;

import com.doan1.marketplace.dto.response.CategoryResponse;
import com.doan1.marketplace.dto.response.InternalProviderResponse;
import com.doan1.marketplace.dto.response.ProviderResponse;
import com.doan1.marketplace.dto.response.ReviewResponse;
import com.doan1.marketplace.entity.ProviderReview;
import com.doan1.marketplace.entity.ProviderProfile;

import java.util.List;

public final class MarketplaceMapper {
    private MarketplaceMapper() {
    }

    public static ProviderResponse toResponse(ProviderProfile profile) {
        List<CategoryResponse> categories = CategoryResponse.forIds(profile.getCategoryIds());
        return new ProviderResponse(
                profile.getId(),
                profile.getFullName(),
                categories.stream().map(CategoryResponse::id).toList(),
                categories,
                profile.getAddress(),
                profile.getImageUrl(),
                profile.getDescription(),
                profile.getStatus(),
                profile.getRating(),
                profile.getReviewCount(),
                profile.isDemoFixture());
    }

    public static InternalProviderResponse toInternalResponse(ProviderProfile profile) {
        List<CategoryResponse> categories = CategoryResponse.forIds(profile.getCategoryIds());
        return new InternalProviderResponse(
                profile.getId(),
                profile.getIdentityAccountId(),
                profile.getFullName(),
                categories.stream().map(CategoryResponse::id).toList(),
                categories,
                profile.getStatus().name());
    }

    public static ReviewResponse toResponse(ProviderReview review) {
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
