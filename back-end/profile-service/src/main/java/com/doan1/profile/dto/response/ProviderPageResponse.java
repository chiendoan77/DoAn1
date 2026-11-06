package com.doan1.profile.dto.response;

import java.util.List;

public record ProviderPageResponse(
        List<ProviderResponse> content,
        long totalElements,
        int totalPages,
        int size,
        int number) {
}
