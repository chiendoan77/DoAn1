package com.doan1.booking.dto;

import java.util.List;
import java.util.UUID;

public record ProviderDetailsResponse(
        UUID id,
        UUID identityAccountId,
        String fullName,
        List<String> categoryIds,
        List<MarketplaceCategoryResponse> categories,
        String status) {
}
