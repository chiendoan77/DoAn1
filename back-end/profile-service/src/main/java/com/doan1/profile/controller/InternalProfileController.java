package com.doan1.profile.controller;

import com.doan1.profile.dto.response.InternalProviderResponse;
import com.doan1.profile.dto.response.ProviderEligibilityResponse;
import com.doan1.profile.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@RestController
public class InternalProfileController {
    private final ProfileService profiles;
    private final byte[] bookingApiKey;
    private final byte[] catalogApiKey;
    private final byte[] reviewApiKey;

    public InternalProfileController(ProfileService profiles,
            @Value("${service.api-keys.booking:}") String bookingApiKey,
            @Value("${service.api-keys.catalog:}") String catalogApiKey,
            @Value("${service.api-keys.review:}") String reviewApiKey) {
        this.profiles = profiles;
        this.bookingApiKey = bookingApiKey.getBytes(StandardCharsets.UTF_8);
        this.catalogApiKey = catalogApiKey.getBytes(StandardCharsets.UTF_8);
        this.reviewApiKey = reviewApiKey.getBytes(StandardCharsets.UTF_8);
    }

    @GetMapping("/internal/providers/{providerId}")
    public InternalProviderResponse getApprovedProvider(HttpServletRequest request,
            @PathVariable UUID providerId, @RequestParam String categoryId) {
        verify(request, bookingApiKey);
        return profiles.internalProvider(providerId, categoryId);
    }

    @GetMapping("/internal/profiles/providers/{accountId}/eligibility")
    public ProviderEligibilityResponse eligibility(HttpServletRequest request, @PathVariable UUID accountId) {
        verify(request, catalogApiKey);
        return profiles.eligibility(accountId);
    }

    @PostMapping("/internal/profiles/providers/{providerId}/rating-reviews")
    public void addRatingReview(HttpServletRequest request, @PathVariable UUID providerId,
            @RequestBody RatingReviewRequest input) {
        verify(request, reviewApiKey);
        if (input.reviewId() == null || input.rating() < 1 || input.rating() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid review rating.");
        }
        profiles.addRatingReview(input.reviewId(), providerId, input.rating());
    }

    private static void verify(HttpServletRequest request, byte[] expectedKey) {
        String provided = request.getHeader("X-Service-Api-Key");
        if (expectedKey.length < 32) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Internal API key is not configured.");
        }
        if (provided == null || !MessageDigest.isEqual(expectedKey, provided.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid internal API key.");
        }
    }

    public record RatingReviewRequest(UUID reviewId, int rating) {}
}
