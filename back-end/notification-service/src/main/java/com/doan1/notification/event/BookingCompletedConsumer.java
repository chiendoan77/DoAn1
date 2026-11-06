package com.doan1.notification.event;

import com.doan1.notification.entity.Notification;
import com.doan1.notification.repository.NotificationRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class BookingCompletedConsumer {
    private final NotificationRepository notifications;

    public BookingCompletedConsumer(NotificationRepository notifications) {
        this.notifications = notifications;
    }

    @RabbitListener(queues = "notifications.booking")
    @Transactional
    public void receive(BookingCompletedEvent event) {
        saveFor(event, event.customerAccountId(), "CUSTOMER",
                "Lịch hẹn với " + event.providerName() + " đã hoàn tất.");
        saveFor(event, event.providerAccountId(), "PROVIDER",
                "Lịch hẹn với " + event.customerName() + " đã hoàn tất.");
    }

    private void saveFor(BookingCompletedEvent event, UUID recipientId, String role, String message) {
        UUID notificationEventId = UUID.nameUUIDFromBytes(
                (event.eventId() + ":" + recipientId).getBytes(StandardCharsets.UTF_8));
        if (notifications.existsByEventId(notificationEventId)) return;
        notifications.save(new Notification(notificationEventId, recipientId, role, event.eventType(),
                event.bookingId(), "BOOKING", message));
    }
}
