package com.doan1.marketplace.dto;

import com.doan1.marketplace.model.ProviderProfile;

import java.util.List;
import java.util.UUID;

public record InternalProviderResponse(
        UUID id,
        UUID identityAccountId,
        String fullName,
        List<String> categoryIds,
        List<CategoryResponse> categories,
        String status) {
    public static InternalProviderResponse from(ProviderProfile profile) {
        List<CategoryResponse> categories = CategoryResponse.forIds(profile.getCategoryIds());
        return new InternalProviderResponse(
                profile.getId(),
                profile.getIdentityAccountId(),
                profile.getFullName(),
                categories.stream().map(CategoryResponse::id).toList(),
                categories,
                profile.getStatus().name());
    }
}
