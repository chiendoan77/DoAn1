package com.doan1.marketplace.dto.response;

import java.util.List;
import java.util.UUID;

public record InternalProviderResponse(
        UUID id,
        UUID identityAccountId,
        String fullName,
        List<String> categoryIds,
        List<CategoryResponse> categories,
        String status) {
}
