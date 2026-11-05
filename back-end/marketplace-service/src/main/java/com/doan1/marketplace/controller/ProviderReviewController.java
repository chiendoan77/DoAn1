package com.doan1.marketplace.controller;

import com.doan1.marketplace.client.BookingReviewEligibilityClient;
import com.doan1.marketplace.dto.request.CreateReviewRequest;
import com.doan1.marketplace.dto.response.ReviewEligibility;
import com.doan1.marketplace.dto.response.ReviewResponse;
import com.doan1.marketplace.entity.ProviderReview;
import com.doan1.marketplace.mapper.MarketplaceMapper;
import com.doan1.marketplace.repository.ProviderReviewRepository;
import com.doan1.marketplace.security.JwtPrincipalService;
import com.doan1.marketplace.service.ProviderReviewService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ProviderReviewController {
    private final JwtPrincipalService principals;
    private final BookingReviewEligibilityClient bookingEligibility;
    private final ProviderReviewService reviewService;
    private final ProviderReviewRepository reviews;

    public ProviderReviewController(
            JwtPrincipalService principals,
            BookingReviewEligibilityClient bookingEligibility,
            ProviderReviewService reviewService,
            ProviderReviewRepository reviews) {
        this.principals = principals;
        this.bookingEligibility = bookingEligibility;
        this.reviewService = reviewService;
        this.reviews = reviews;
    }

    @PostMapping
    public ReviewResponse add(HttpServletRequest request, @Valid @RequestBody CreateReviewRequest input) {
        JwtPrincipalService.Principal reviewer = principals.authenticate(request);
        if (!"CUSTOMER".equals(reviewer.role()) && !"PROVIDER".equals(reviewer.role())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vai trò này không thể gửi đánh giá.");
        }
        ReviewEligibility eligibility =
                bookingEligibility.check(input.bookingId(), reviewer.accountId(), reviewer.role());
        ProviderReview review = reviewService.addReview(
                input.bookingId(),
                reviewer.accountId(),
                reviewer.role(),
                eligibility,
                input.rating(),
                input.comment());
        return MarketplaceMapper.toResponse(review);
    }

    @GetMapping("/mine")
    public List<ReviewResponse> mine(HttpServletRequest request) {
        JwtPrincipalService.Principal reviewer = principals.authenticate(request);
        return reviews.findByReviewerAccountIdOrderByCreatedAtDesc(reviewer.accountId())
                .stream().map(MarketplaceMapper::toResponse).toList();
    }

}
