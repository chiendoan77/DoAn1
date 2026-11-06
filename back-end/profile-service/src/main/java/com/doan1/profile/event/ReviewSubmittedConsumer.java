package com.doan1.profile.event;

import com.doan1.profile.service.ProfileService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReviewSubmittedConsumer {
    private final ProfileService profiles;

    public ReviewSubmittedConsumer(ProfileService profiles) {
        this.profiles = profiles;
    }

    @RabbitListener(queues = "profile.reviews")
    @Transactional
    public void receive(ReviewSubmittedEvent event) {
        if ("PROVIDER".equals(event.targetRole())) {
            profiles.addRatingReview(event.reviewId(), event.targetId(), event.rating());
        }
    }

    public record ReviewSubmittedEvent(
            java.util.UUID eventId,
            String eventType,
            java.util.UUID reviewId,
            java.util.UUID targetId,
            String targetRole,
            int rating,
            java.time.Instant occurredAt) {
    }
}
