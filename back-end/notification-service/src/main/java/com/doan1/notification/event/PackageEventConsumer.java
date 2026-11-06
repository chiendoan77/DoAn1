package com.doan1.notification.event;

import com.doan1.notification.entity.Notification;
import com.doan1.notification.repository.NotificationRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PackageEventConsumer {
    private final NotificationRepository notifications;

    public PackageEventConsumer(NotificationRepository notifications) {
        this.notifications = notifications;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    @Transactional
    public void receive(PackageEvent event) {
        if (notifications.existsByEventId(event.eventId())) return;
        boolean admin = "PackageSubmitted".equals(event.eventType())
                || "PackageResubmitted".equals(event.eventType());
        String role = admin ? "ADMIN" : "PROVIDER";
        String message = switch (event.eventType()) {
            case "PackageSubmitted" -> "Có gói dịch vụ mới cần kiểm duyệt.";
            case "PackageResubmitted" -> "Có gói dịch vụ được gửi lại để kiểm duyệt.";
            case "PackageApproved" -> "Gói dịch vụ đã được duyệt và có thể hiển thị công khai.";
            case "PackageRejected" -> "Gói dịch vụ bị từ chối: " + (event.reason() == null ? "" : event.reason());
            case "PackageDeactivated" -> "Gói dịch vụ đã được ngừng hoạt động.";
            default -> throw new IllegalArgumentException("Unsupported package event: " + event.eventType());
        };
        notifications.save(new Notification(event.eventId(), admin ? event.adminId() : event.providerId(),
                role, event.eventType(), event.packageId(), message));
    }
}
