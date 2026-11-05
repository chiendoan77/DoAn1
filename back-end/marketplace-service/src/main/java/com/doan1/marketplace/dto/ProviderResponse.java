package com.doan1.marketplace.dto;

import com.doan1.marketplace.model.ProviderProfile;
import com.doan1.marketplace.model.ProviderStatus;

import java.util.List;
import java.util.UUID;

public record ProviderResponse(
        UUID id,
        String fullName,
        List<String> categoryIds,
        List<CategoryResponse> categories,
        String address,
        String imageUrl,
        String description,
        ProviderStatus status,
        double rating,
        int reviewCount,
        boolean demoData) {
    public static ProviderResponse from(ProviderProfile profile) {
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
}
