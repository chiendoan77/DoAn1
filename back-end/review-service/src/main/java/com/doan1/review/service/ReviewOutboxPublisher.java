package com.doan1.review.service;

import com.doan1.review.entity.ReviewOutboxEvent;
import com.doan1.review.repository.ReviewOutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReviewOutboxPublisher {
    private static final Logger logger = LoggerFactory.getLogger(ReviewOutboxPublisher.class);
    private final ReviewOutboxEventRepository events;
    private final RabbitTemplate rabbit;
    private final Set<UUID> inFlight = ConcurrentHashMap.newKeySet();

    public ReviewOutboxPublisher(ReviewOutboxEventRepository events, RabbitTemplate rabbit) {
        this.events = events;
        this.rabbit = rabbit;
        rabbit.setMandatory(true);
    }

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:1000}")
    public void publishPending() {
        for (ReviewOutboxEvent event : events.findTop50ByStatusOrderByCreatedAtAsc("PENDING")) {
            if (inFlight.add(event.getId())) publish(event);
        }
    }

    private void publish(ReviewOutboxEvent event) {
        CorrelationData correlation = new CorrelationData(event.getId().toString());
        try {
            rabbit.convertAndSend("doan1.events", "review.submitted", event.getPayload(), message -> {
                message.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            }, correlation);
            correlation.getFuture().whenComplete((confirm, failure) -> {
                try {
                    if (failure != null || confirm == null || !confirm.isAck() || correlation.getReturned() != null) {
                        logger.error("Review event {} was not routed and confirmed; it will be retried.", event.getId(), failure);
                        return;
                    }
                    events.findById(event.getId()).ifPresent(pending -> {
                        pending.markPublished(Instant.now());
                        events.save(pending);
                    });
                } catch (RuntimeException exception) {
                    logger.error("Could not mark review event {} as published; it will be retried.", event.getId(), exception);
                } finally {
                    inFlight.remove(event.getId());
                }
            });
        } catch (RuntimeException exception) {
            inFlight.remove(event.getId());
            logger.error("Could not publish review event {}; it will be retried.", event.getId(), exception);
        }
    }
}
