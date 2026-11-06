package com.doan1.profile.repository;

import com.doan1.profile.entity.ProviderRatingReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProviderRatingReviewRepository extends JpaRepository<ProviderRatingReview, UUID> {
}
