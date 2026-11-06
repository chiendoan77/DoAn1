package com.doan1.booking.client;

import com.doan1.booking.dto.response.PackageDetailsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class CatalogClient {
    private final RestClient restClient;
    private final String serviceApiKey;

    public CatalogClient(
            RestClient.Builder builder,
            @Value("${services.catalog.url:http://localhost:8086}") String catalogUrl,
            @Value("${service.api-keys.catalog:}") String serviceApiKey) {
        this.restClient = builder.baseUrl(catalogUrl).build();
        this.serviceApiKey = serviceApiKey;
    }

    public PackageDetailsResponse getApprovedPackage(UUID packageId) {
        if (serviceApiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Booking-Catalog API key is not configured.");
        }
        try {
            PackageDetailsResponse result = restClient.get()
                    .uri("/internal/catalog/packages/{id}", packageId)
                    .header("X-Service-Api-Key", serviceApiKey)
                    .retrieve()
                    .body(PackageDetailsResponse.class);
            if (result == null || result.id() == null || result.providerId() == null
                    || result.categoryId() == null || result.price() == null || !"APPROVED".equals(result.status())) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Catalog returned an invalid package.");
            }
            return result;
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().is4xxClientError()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gói dịch vụ không còn khả dụng.");
            }
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Không thể kiểm tra gói dịch vụ lúc này.");
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Không thể kết nối Catalog.");
        }
    }
}
