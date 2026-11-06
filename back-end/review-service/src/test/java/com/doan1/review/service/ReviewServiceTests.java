package com.doan1.review.service;

import com.doan1.review.client.BookingReviewEligibilityClient;
import com.doan1.review.dto.ReviewEligibility;
import com.doan1.review.entity.Review;
import com.doan1.review.entity.ReviewOutboxEvent;
import com.doan1.review.repository.ReviewOutboxEventRepository;
import com.doan1.review.repository.ReviewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReviewServiceTests {
    private ReviewRepository reviews;
    private ReviewOutboxEventRepository outbox;
    private BookingReviewEligibilityClient booking;
    private ReviewService service;

    @BeforeEach
    void setUp() {
        reviews = mock(ReviewRepository.class);
        outbox = mock(ReviewOutboxEventRepository.class);
        booking = mock(BookingReviewEligibilityClient.class);
        service = new ReviewService(reviews, outbox, booking, new ObjectMapper().findAndRegisterModules());
    }

    @Test
    void savesEligibleReviewAndTransactionalOutboxEvent() throws Exception {
        UUID reviewerId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        when(reviews.existsByBookingIdAndReviewerAccountId(bookingId, reviewerId)).thenReturn(false);
        when(booking.check(bookingId, reviewerId, "CUSTOMER"))
                .thenReturn(new ReviewEligibility(true, targetId, "PROVIDER"));
        when(reviews.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(outbox.save(any(ReviewOutboxEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.add(reviewerId, "CUSTOMER", bookingId, 5, "  Great work  ");

        assertEquals(bookingId, response.bookingId());
        assertEquals(targetId, response.targetId());
        assertEquals("Great work", response.comment());
        var savedEvent = forClass(ReviewOutboxEvent.class);
        verify(outbox).save(savedEvent.capture());
        var payload = new ObjectMapper().readTree(savedEvent.getValue().getPayload());
        assertEquals("ReviewSubmitted", payload.get("eventType").asText());
        assertEquals(response.id().toString(), payload.get("reviewId").asText());
    }

    @Test
    void rejectsDuplicateReviewBeforeCheckingBooking() {
        UUID reviewerId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();
        when(reviews.existsByBookingIdAndReviewerAccountId(bookingId, reviewerId)).thenReturn(true);

        assertThrows(ResponseStatusException.class,
                () -> service.add(reviewerId, "CUSTOMER", bookingId, 4, "Already reviewed"));

        verify(booking, never()).check(bookingId, reviewerId, "CUSTOMER");
        verify(reviews, never()).save(any(Review.class));
        verify(outbox, never()).save(any(ReviewOutboxEvent.class));
    }
}
