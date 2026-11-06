package com.doan1.catalog.service;

import com.doan1.catalog.entity.OutboxEvent;
import com.doan1.catalog.enums.OutboxStatus;
import com.doan1.catalog.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(name = "app.outbox.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxPublisher {
    private static final Logger logger = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxEventRepository outbox;
    private final RabbitTemplate rabbitTemplate;
    private final Set<UUID> inFlight = ConcurrentHashMap.newKeySet();

    public OutboxPublisher(OutboxEventRepository outbox, RabbitTemplate rabbitTemplate) {
        this.outbox = outbox;
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setMandatory(true);
    }

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:1000}")
    public void publishPending() {
        for (OutboxEvent event : outbox.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING)) {
            if (inFlight.add(event.getId())) {
                publish(event);
            }
        }
    }

    private void publish(OutboxEvent event) {
        CorrelationData correlation = new CorrelationData(event.getId().toString());
        try {
            rabbitTemplate.convertAndSend(
                    event.getExchangeName(),
                    event.getRoutingKey(),
                    event.getPayload(),
                    message -> {
                        message.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
                        message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return message;
                    },
                    correlation);
            correlation.getFuture().whenComplete((confirm, failure) -> {
                try {
                    if (failure != null) {
                        logger.error("Catalog event {} was not confirmed by RabbitMQ.", event.getId(), failure);
                    } else if (confirm == null || !confirm.isAck()) {
                        logger.error("Catalog event {} was rejected by RabbitMQ: {}",
                                event.getId(), confirm == null ? "missing publisher confirmation" : confirm.getReason());
                    } else if (correlation.getReturned() != null) {
                        logger.error("Catalog event {} was returned by RabbitMQ: {}",
                                event.getId(), correlation.getReturned().getReplyText());
                    } else {
                        try {
                            outbox.findById(event.getId()).ifPresent(pending -> {
                                pending.markPublished(Instant.now());
                                outbox.save(pending);
                            });
                        } catch (RuntimeException exception) {
                            logger.error("Catalog event {} was delivered but could not be marked published; "
                                    + "it will be retried.", event.getId(), exception);
                        }
                    }
                } finally {
                    inFlight.remove(event.getId());
                }
            });
        } catch (RuntimeException exception) {
            inFlight.remove(event.getId());
            logger.error("Could not publish catalog event {}; it will be retried.", event.getId(), exception);
        }
    }
}
