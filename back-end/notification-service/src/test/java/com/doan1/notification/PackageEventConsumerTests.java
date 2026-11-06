package com.doan1.notification;

import com.doan1.notification.entity.Notification;
import com.doan1.notification.event.PackageEvent;
import com.doan1.notification.event.PackageEventConsumer;
import com.doan1.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PackageEventConsumerTests {
    @Test
    void duplicateEventDoesNotCreateDuplicateNotification() {
        NotificationRepository repository = mock(NotificationRepository.class);
        PackageEventConsumer consumer = new PackageEventConsumer(repository);
        UUID eventId = UUID.randomUUID();
        PackageEvent event = new PackageEvent(eventId, "PackageApproved", UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), null, Instant.now());
        when(repository.existsByEventId(eventId)).thenReturn(false, true);

        consumer.receive(event);
        consumer.receive(event);

        verify(repository, times(1)).save(any(Notification.class));
    }

    @Test
    void rejectedEventIncludesAdminReasonInProviderNotification() {
        NotificationRepository repository = mock(NotificationRepository.class);
        PackageEventConsumer consumer = new PackageEventConsumer(repository);
        PackageEvent event = new PackageEvent(UUID.randomUUID(), "PackageRejected", UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), "Thiếu thông tin", Instant.now());

        consumer.receive(event);

        verify(repository).save(argThat(notification ->
                notification.getRecipientId().equals(event.providerId())
                        && notification.getRecipientRole().equals("PROVIDER")
                        && notification.getMessage().contains("Thiếu thông tin")));
    }
}
