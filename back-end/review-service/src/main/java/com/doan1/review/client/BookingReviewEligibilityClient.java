package com.doan1.review.client;

import com.doan1.review.dto.ReviewEligibility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;

@Component
public class BookingReviewEligibilityClient {
    private final RestClient rest;
    private final String key;

    public BookingReviewEligibilityClient(RestClient.Builder builder,
            @Value("${services.booking.url}") String bookingUrl,
            @Value("${service.api-keys.booking:}") String key) {
        rest = builder.baseUrl(bookingUrl).build();
        this.key = key;
    }

    public ReviewEligibility check(UUID bookingId, UUID accountId, String role) {
        if (key.isBlank()) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Thiếu Booking API key.");
        try {
            ReviewEligibility result = rest.get()
                    .uri(uri -> uri.path("/internal/bookings/{id}/review-eligibility")
                            .queryParam("reviewerAccountId", accountId).queryParam("reviewerRole", role).build(bookingId))
                    .header("X-Service-Api-Key", key).retrieve().body(ReviewEligibility.class);
            if (result == null || !result.eligible()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ người tham gia được đánh giá booking đã hoàn tất.");
            }
            return result;
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().is4xxClientError()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Booking chưa hoàn tất hoặc bạn không tham gia.");
            }
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Không thể kiểm tra trạng thái booking.");
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Không thể kết nối Booking.");
        }
    }
}
