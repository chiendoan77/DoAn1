package com.doan1.catalog.dto.response;

import com.doan1.catalog.enums.PackageStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PackageResponse(
        UUID id,
        UUID providerId,
        String categoryId,
        String name,
        String description,
        BigDecimal price,
        int durationMinutes,
        String providerPhone,
        PackageStatus status,
        String rejectionReason,
        long version,
        Instant createdAt,
        Instant updatedAt) {
}
