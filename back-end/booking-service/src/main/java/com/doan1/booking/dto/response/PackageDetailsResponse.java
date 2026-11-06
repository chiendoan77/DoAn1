package com.doan1.booking.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record PackageDetailsResponse(
        UUID id,
        UUID providerId,
        String categoryId,
        String name,
        BigDecimal price,
        int durationMinutes,
        String status) {
}
