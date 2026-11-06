package com.doan1.marketplace.controller;

import com.doan1.marketplace.dto.response.ReviewResponse;
import com.doan1.marketplace.mapper.MarketplaceMapper;
import com.doan1.marketplace.repository.ProviderReviewRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@RestController
public class InternalReviewMigrationController {
    private final ProviderReviewRepository reviews;
    private final byte[] migrationKey;

    public InternalReviewMigrationController(ProviderReviewRepository reviews,
            @Value("${service.api-keys.review-migration:}") String migrationKey) {
        this.reviews = reviews;
        this.migrationKey = migrationKey.getBytes(StandardCharsets.UTF_8);
    }

    @GetMapping("/internal/migration/reviews")
    public List<ReviewResponse> export(HttpServletRequest request) {
        String suppliedKey = request.getHeader("X-Service-Api-Key");
        if (migrationKey.length < 32) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Review migration key is not configured.");
        }
        if (suppliedKey == null || !MessageDigest.isEqual(
                migrationKey, suppliedKey.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A valid migration API key is required.");
        }
        return reviews.findAll().stream().map(MarketplaceMapper::toResponse).toList();
    }
}
