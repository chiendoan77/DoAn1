package com.doan1.marketplace.repository;

import com.doan1.marketplace.entity.ProviderReview;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProviderReviewRepository extends JpaRepository<ProviderReview, UUID> {
    boolean existsByBookingIdAndReviewerAccountId(UUID bookingId, UUID reviewerAccountId);

    List<ProviderReview> findByReviewerAccountIdOrderByCreatedAtDesc(UUID reviewerAccountId);

    @Query("""
            select avg(review.rating), count(review)
            from ProviderReview review
            where review.targetId = :targetId and review.targetRole = 'PROVIDER'
            """)
    List<Object[]> ratingSummary(@Param("targetId") UUID targetId);
}
