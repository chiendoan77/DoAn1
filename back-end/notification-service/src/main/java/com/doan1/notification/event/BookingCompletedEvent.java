package com.doan1.notification.event;

import java.util.UUID;

public record BookingCompletedEvent(
        UUID eventId,
        String eventType,
        UUID bookingId,
        UUID customerAccountId,
        UUID providerAccountId,
        String customerName,
        String providerName,
        String packageName) {
}
