package com.doan1.notification;

import com.doan1.notification.event.PackageEvent;
import com.doan1.notification.event.PackageEventConsumer;
import com.doan1.notification.service.NotificationService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;

class PackageEventConsumerTests {
    @Test
    void forwardsPackageApprovedEventToService() {
        NotificationService service = mock(NotificationService.class);
        PackageEventConsumer consumer = new PackageEventConsumer(service);
        UUID eventId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        UUID packageId = UUID.randomUUID();
        PackageEvent event = new PackageEvent(eventId, "PackageApproved", packageId,
                providerId, UUID.randomUUID(), null, Instant.now());

        consumer.receive(event);

        verify(service).savePackageNotification(
                eq(eventId), eq(providerId), eq("PROVIDER"), eq("PackageApproved"),
                eq(packageId), contains("Gói dịch vụ đã được duyệt"));
    }

    @Test
    void rejectedEventIncludesAdminReasonInProviderNotification() {
        NotificationService service = mock(NotificationService.class);
        PackageEventConsumer consumer = new PackageEventConsumer(service);
        UUID eventId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        UUID packageId = UUID.randomUUID();
        PackageEvent event = new PackageEvent(eventId, "PackageRejected", packageId,
                providerId, UUID.randomUUID(), "Thiếu thông tin", Instant.now());

        consumer.receive(event);

        verify(service).savePackageNotification(
                eq(eventId), eq(providerId), eq("PROVIDER"), eq("PackageRejected"),
                eq(packageId), contains("Thiếu thông tin"));
    }
}
