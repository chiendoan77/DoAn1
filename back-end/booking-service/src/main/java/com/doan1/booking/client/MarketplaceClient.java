package com.doan1.booking.client;

import com.doan1.booking.dto.response.ProviderDetailsResponse;
import com.doan1.booking.entity.Booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class MarketplaceClient {
    private final RestClient restClient;
    private final String serviceApiKey;

    public MarketplaceClient(
            RestClient.Builder builder,
            @Value("${services.marketplace.url}") String marketplaceUrl,
            @Value("${service.api-key:}") String serviceApiKey) {
        this.restClient = builder.baseUrl(marketplaceUrl).build();
        this.serviceApiKey = serviceApiKey;
    }

    public ProviderDetailsResponse getApprovedProvider(String categoryId, UUID providerId) {
        if (serviceApiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Thiếu cấu hình API key nội bộ giữa Booking và Marketplace.");
        }
        try {
            ProviderDetailsResponse details = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/internal/providers/{providerId}")
                            .queryParam("categoryId", categoryId)
                            .build(providerId))
                    .header("X-Service-Api-Key", serviceApiKey)
                    .retrieve()
                    .body(ProviderDetailsResponse.class);
            if (details == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Marketplace trả về dữ liệu nhà cung cấp không hợp lệ.");
            }
            return details;
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().is4xxClientError()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nhà cung cấp không khả dụng cho dịch vụ này.");
            }
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Không thể kiểm tra nhà cung cấp lúc này.");
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Không thể kết nối Marketplace.");
        }
    }

}
