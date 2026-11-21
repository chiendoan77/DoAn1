package com.doan1.notification.service;

import com.doan1.notification.dto.NotificationResponse;
import com.doan1.notification.entity.Notification;
import com.doan1.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notifications;

    public NotificationService(NotificationRepository notifications) {
        this.notifications = notifications;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> forUser(UUID accountId) {
        return notifications.findByRecipientIdOrderByCreatedAtDesc(accountId)
                .stream().map(NotificationService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> forAdmin() {
        return notifications.findByRecipientRoleOrderByCreatedAtDesc("ADMIN")
                .stream().map(NotificationService::toResponse).toList();
    }

    @Transactional
    public void savePackageNotification(UUID eventId, UUID recipientId, String recipientRole,
            String eventType, UUID packageId, String message) {
        if (notifications.existsByEventId(eventId)) return;
        notifications.save(new Notification(eventId, recipientId, recipientRole, eventType, packageId, message));
    }

    @Transactional
    public void saveBookingNotification(UUID notificationEventId, UUID recipientId, String recipientRole,
            String eventType, UUID relatedId, String relatedType, String message) {
        if (notifications.existsByEventId(notificationEventId)) return;
        notifications.save(new Notification(notificationEventId, recipientId, recipientRole, eventType,
                relatedId, relatedType, message));
    }

    private static NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(), n.getEventType(), n.getPackageId(),
                n.getRelatedId(), n.getRelatedType(), n.getMessage(), n.getCreatedAt());
    }
}
