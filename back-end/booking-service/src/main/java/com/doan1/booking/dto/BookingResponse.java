package com.doan1.booking.dto;

import com.doan1.booking.model.Booking;
import com.doan1.booking.model.BookingStatus;

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
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getCategoryId(),
                booking.getCategoryName(),
                booking.getProviderProfileId(),
                booking.getProviderName(),
                booking.getCustomerName(),
                booking.getScheduledAt(),
                booking.getAddress(),
                booking.getDescription(),
                booking.getStatus(),
                booking.getCreatedAt());
    }
}
