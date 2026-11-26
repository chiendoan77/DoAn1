package com.doan1.review.repository;

import com.doan1.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    boolean existsByBookingIdAndReviewerAccountId(UUID bookingId, UUID reviewerAccountId);
    List<Review> findByReviewerAccountIdOrderByCreatedAtDesc(UUID reviewerAccountId);
    Page<Review> findByReviewerAccountIdOrderByCreatedAtDesc(UUID reviewerAccountId, Pageable pageable);
}
