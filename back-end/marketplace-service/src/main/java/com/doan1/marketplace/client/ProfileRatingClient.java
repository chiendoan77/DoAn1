package com.doan1.marketplace.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class ProfileRatingClient {
    private final RestClient client;
    private final String apiKey;

    public ProfileRatingClient(RestClient.Builder builder,
            @Value("${services.profile.url:http://localhost:8085}") String profileUrl,
            @Value("${service.api-keys.profile:}") String apiKey) {
        this.client = builder.baseUrl(profileUrl).build();
        this.apiKey = apiKey;
    }

    public void addReviewRating(UUID reviewId, UUID providerId, int rating) {
        if (apiKey.length() < 32) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Thiếu API key nội bộ tới Profile.");
        }
        try {
            client.post()
                    .uri("/internal/profiles/providers/{providerId}/rating-reviews", providerId)
                    .header("X-Service-Api-Key", apiKey)
                    .body(new RatingReviewRequest(reviewId, rating))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().is4xxClientError()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Không thể cập nhật điểm nhà cung cấp.");
            }
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Không thể cập nhật điểm nhà cung cấp.");
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Không thể kết nối Profile.");
        }
    }

    private record RatingReviewRequest(UUID reviewId, int rating) {}
}
