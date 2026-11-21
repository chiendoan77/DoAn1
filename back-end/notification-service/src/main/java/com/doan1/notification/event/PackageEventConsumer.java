package com.doan1.notification.event;

import com.doan1.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PackageEventConsumer {

    private final NotificationService notificationService;

    public PackageEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void receive(PackageEvent event) {
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
        UUID recipientId = admin ? event.adminId() : event.providerId();
        notificationService.savePackageNotification(
                event.eventId(), recipientId, role, event.eventType(), event.packageId(), message);
    }
}
