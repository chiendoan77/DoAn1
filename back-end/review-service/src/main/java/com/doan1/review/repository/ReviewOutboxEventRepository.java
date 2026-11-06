package com.doan1.review.repository;

import com.doan1.review.entity.ReviewOutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ReviewOutboxEventRepository extends JpaRepository<ReviewOutboxEvent, UUID> {
    List<ReviewOutboxEvent> findTop50ByStatusOrderByCreatedAtAsc(String status);
}
