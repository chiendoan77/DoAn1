package com.doan1.notification.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id, String eventType, UUID packageId, UUID relatedId, String relatedType, String message, Instant createdAt) {
}
