package com.doan1.booking.controller;

import com.doan1.booking.dto.response.ReviewEligibilityResponse;
import com.doan1.booking.service.BookingService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@RestController
@RequestMapping("/internal/bookings")
public class InternalReviewEligibilityController {
    private final BookingService bookings;
    private final byte[] serviceApiKey;

    public InternalReviewEligibilityController(
            BookingService bookings,
            @Value("${service.api-key:}") String serviceApiKey) {
        this.bookings = bookings;
        this.serviceApiKey = serviceApiKey.getBytes(StandardCharsets.UTF_8);
    }

    @GetMapping("/{bookingId}/review-eligibility")
    public ReviewEligibilityResponse check(
            HttpServletRequest request,
            @PathVariable UUID bookingId,
            @RequestParam UUID reviewerAccountId,
            @RequestParam String reviewerRole) {
        String providedKey = request.getHeader("X-Service-Api-Key");
        if (serviceApiKey.length < 32) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Internal API key is not configured.");
        }
        if (providedKey == null || !MessageDigest.isEqual(
                serviceApiKey, providedKey.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid internal API key.");
        }

        return bookings.reviewEligibility(bookingId, reviewerAccountId, reviewerRole);
    }
}
