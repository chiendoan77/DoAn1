package com.doan1.profile.dto.response;

import com.doan1.profile.enums.ProviderStatus;
import java.util.List;
import java.util.UUID;

public record ProviderResponse(UUID id, String fullName, List<String> categoryIds,
        List<CategoryResponse> categories, String address, String imageUrl, String description,
        ProviderStatus status, double rating, int reviewCount, boolean demoData) {
}
