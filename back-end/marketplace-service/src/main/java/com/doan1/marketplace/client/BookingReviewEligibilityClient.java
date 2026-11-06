package com.doan1.marketplace.client;

import com.doan1.marketplace.dto.response.ReviewEligibility;

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
    private final RestClient restClient;
    private final String serviceApiKey;

    public BookingReviewEligibilityClient(
            RestClient.Builder builder,
            @Value("${services.booking.url}") String bookingUrl,
            @Value("${service.api-keys.booking:}") String serviceApiKey) {
        this.restClient = builder.baseUrl(bookingUrl).build();
        this.serviceApiKey = serviceApiKey;
    }

    public ReviewEligibility check(UUID bookingId, UUID reviewerAccountId, String reviewerRole) {
        if (serviceApiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Thiếu cấu hình API key nội bộ giữa Marketplace và Booking.");
        }
        try {
            ReviewEligibility result = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/internal/bookings/{bookingId}/review-eligibility")
                            .queryParam("reviewerAccountId", reviewerAccountId)
                            .queryParam("reviewerRole", reviewerRole)
                            .build(bookingId))
                    .header("X-Service-Api-Key", serviceApiKey)
                    .retrieve()
                    .body(ReviewEligibility.class);
            if (result == null || !result.eligible()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ người tham gia mới có thể đánh giá booking đã hoàn tất.");
            }
            return result;
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().is4xxClientError()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Booking chưa hoàn tất hoặc bạn không tham gia booking này.");
            }
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Không thể kiểm tra trạng thái booking.");
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Không thể kết nối Booking.");
        }
    }
}
