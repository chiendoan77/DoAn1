package com.doan1.review.controller;

import com.doan1.review.dto.ReviewRequest;
import com.doan1.review.dto.ReviewResponse;
import com.doan1.review.security.JwtPrincipalService;
import com.doan1.review.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@RestController
public class ReviewController {
    private final ReviewService reviews;
    private final JwtPrincipalService principals;
    private final byte[] migrationKey;

    public ReviewController(ReviewService reviews, JwtPrincipalService principals,
            @Value("${service.api-keys.migration:}") String migrationKey) {
        this.reviews = reviews;
        this.principals = principals;
        this.migrationKey = migrationKey.getBytes(StandardCharsets.UTF_8);
    }

    @PostMapping("/api/reviews")
    public ReviewResponse add(HttpServletRequest request, @Valid @RequestBody ReviewRequest input) {
        JwtPrincipalService.Principal reviewer = principals.authenticate(request);
        return reviews.add(reviewer.accountId(), reviewer.role(), input.bookingId(), input.rating(), input.comment());
    }

    @GetMapping("/api/reviews/mine")
    public List<ReviewResponse> mine(HttpServletRequest request) {
        return reviews.mine(principals.authenticate(request).accountId());
    }

    @PostMapping("/internal/migration/reviews/import")
    public int importLegacy(@RequestHeader(name = "X-Service-Api-Key", required = false) String suppliedKey,
            @RequestBody List<ReviewResponse> legacyReviews) {
        if (migrationKey.length < 32) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Review migration key is not configured.");
        }
        if (suppliedKey == null || !MessageDigest.isEqual(
                migrationKey, suppliedKey.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A valid migration API key is required.");
        }
        return reviews.importLegacy(legacyReviews);
    }
}
