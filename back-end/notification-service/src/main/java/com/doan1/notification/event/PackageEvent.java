package com.doan1.notification.event;

import java.time.Instant;
import java.util.UUID;

public record PackageEvent(UUID eventId, String eventType, UUID packageId, UUID providerId,
        UUID adminId, String reason, Instant occurredAt) {
}
