package com.doan1.notification.event;

import com.doan1.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class BookingCompletedConsumer {

    private final NotificationService notificationService;

    public BookingCompletedConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "notifications.booking")
    public void receive(BookingCompletedEvent event) {
        saveFor(event, event.customerAccountId(), "CUSTOMER",
                "Lịch hẹn với " + event.providerName() + " đã hoàn tất.");
        saveFor(event, event.providerAccountId(), "PROVIDER",
                "Lịch hẹn với " + event.customerName() + " đã hoàn tất.");
    }

    private void saveFor(BookingCompletedEvent event, UUID recipientId, String role, String message) {
        UUID notificationEventId = UUID.nameUUIDFromBytes(
                (event.eventId() + ":" + recipientId).getBytes(StandardCharsets.UTF_8));
        notificationService.saveBookingNotification(
                notificationEventId, recipientId, role, event.eventType(),
                event.bookingId(), "BOOKING", message);
    }
}
