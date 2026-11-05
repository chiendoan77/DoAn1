package com.doan1.booking.dto.response;

import com.doan1.booking.enums.BookingStatus;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        String categoryId,
        String categoryName,
        UUID providerProfileId,
        String providerName,
        String customerName,
        OffsetDateTime scheduledAt,
        String address,
        String description,
        BookingStatus status,
        Instant createdAt) {
}
