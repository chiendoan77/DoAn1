package com.doan1.profile.mapper;

import com.doan1.profile.dto.response.CategoryResponse;
import com.doan1.profile.dto.response.InternalProviderResponse;
import com.doan1.profile.dto.response.ProviderResponse;
import com.doan1.profile.entity.ProviderProfile;

import java.util.List;

public final class ProfileMapper {
    private ProfileMapper() {}

    public static ProviderResponse toResponse(ProviderProfile profile) {
        List<CategoryResponse> categories = CategoryResponse.forIds(profile.getCategoryIds());
        return new ProviderResponse(profile.getId(), profile.getFullName(),
                categories.stream().map(CategoryResponse::id).toList(), categories,
                profile.getAddress(), profile.getImageUrl(), profile.getDescription(),
                profile.getStatus(), profile.getRating(), profile.getReviewCount(), profile.isDemoFixture());
    }

    public static InternalProviderResponse toInternalResponse(ProviderProfile profile) {
        List<CategoryResponse> categories = CategoryResponse.forIds(profile.getCategoryIds());
        return new InternalProviderResponse(profile.getId(), profile.getIdentityAccountId(), profile.getFullName(),
                categories.stream().map(CategoryResponse::id).toList(), categories, profile.getStatus().name());
    }
}
