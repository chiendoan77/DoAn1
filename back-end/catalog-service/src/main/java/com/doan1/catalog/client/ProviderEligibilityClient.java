package com.doan1.catalog.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.UUID;

@Component
public class ProviderEligibilityClient {
    private final RestClient restClient;
    private final String serviceApiKey;

    public ProviderEligibilityClient(
            RestClient.Builder builder,
            @Value("${services.profile.url}") String profileServiceUrl,
            @Value("${services.profile.api-key:}") String serviceApiKey) {
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(3));
        this.restClient = builder.baseUrl(profileServiceUrl).requestFactory(requestFactory).build();
        this.serviceApiKey = serviceApiKey;
    }

    public UUID requireEligibleProvider(UUID accountId) {
        if (serviceApiKey.isBlank()) {
            throw unavailable();
        }
        try {
            EligibilityResponse response = restClient.get()
                    .uri("/internal/profiles/providers/{accountId}/eligibility", accountId)
                    .header("X-Service-Api-Key", serviceApiKey)
                    .retrieve()
                    .body(EligibilityResponse.class);
            if (response == null || response.eligible() == null) {
                throw unavailable();
            }
            if (!response.eligible()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The provider profile is not eligible.");
            }
            if (response.providerId() == null) {
                throw unavailable();
            }
            return UUID.fromString(response.providerId());
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (RestClientException | IllegalArgumentException exception) {
            throw unavailable();
        }
    }

    private ResponseStatusException unavailable() {
        return new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE, "Provider eligibility could not be verified.");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record EligibilityResponse(Boolean eligible, String providerId) {
    }
}
