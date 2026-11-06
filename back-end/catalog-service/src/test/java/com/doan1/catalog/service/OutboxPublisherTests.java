package com.doan1.catalog.service;

import com.doan1.catalog.entity.OutboxEvent;
import com.doan1.catalog.enums.OutboxStatus;
import com.doan1.catalog.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherTests {
    @Mock
    private OutboxEventRepository outbox;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private OutboxEvent event;
    private OutboxPublisher publisher;

    @BeforeEach
    void setUp() {
        event = new OutboxEvent(
                UUID.randomUUID(), UUID.randomUUID(), "PackageApproved", "{}",
                "doan1.events", "catalog.package.approved");
        when(outbox.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING)).thenReturn(List.of(event));
        publisher = new OutboxPublisher(outbox, rabbitTemplate);
    }

    @Test
    void marksOutboxEventPublishedOnlyAfterRabbitConfirmsDelivery() {
        when(outbox.findById(event.getId())).thenReturn(Optional.of(event));
        ArgumentCaptor<CorrelationData> correlation = ArgumentCaptor.forClass(CorrelationData.class);
        publisher.publishPending();

        verify(rabbitTemplate).convertAndSend(
                eq(event.getExchangeName()),
                eq(event.getRoutingKey()),
                eq(event.getPayload()),
                any(MessagePostProcessor.class),
                correlation.capture());

        correlation.getValue().getFuture().complete(new CorrelationData.Confirm(true, null));

        assertEquals(OutboxStatus.PUBLISHED, event.getStatus());
        assertNotNull(event.getPublishedAt());
        verify(outbox).save(event);
    }

    @Test
    void keepsOutboxEventPendingWhenRabbitRejectsDelivery() {
        ArgumentCaptor<CorrelationData> correlation = ArgumentCaptor.forClass(CorrelationData.class);
        publisher.publishPending();

        verify(rabbitTemplate).convertAndSend(
                eq(event.getExchangeName()),
                eq(event.getRoutingKey()),
                eq(event.getPayload()),
                any(MessagePostProcessor.class),
                correlation.capture());

        correlation.getValue().getFuture().complete(new CorrelationData.Confirm(false, "unroutable"));

        assertEquals(OutboxStatus.PENDING, event.getStatus());
    }
}
