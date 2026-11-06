package com.doan1.profile.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class IdentityAccountClient {
    private final RestClient client;
    private final String apiKey;

    public IdentityAccountClient(RestClient.Builder builder,
            @Value("${services.identity.url}") String identityUrl,
            @Value("${service.api-keys.identity:}") String apiKey) {
        this.client = builder.baseUrl(identityUrl).build();
        this.apiKey = apiKey;
    }

    public boolean isActive(UUID accountId) {
        if (apiKey.length() < 32) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Thiếu API key nội bộ tới Identity.");
        }
        try {
            AccountStatusResponse response = client.get()
                    .uri("/internal/accounts/{accountId}/status", accountId)
                    .header("X-Service-Api-Key", apiKey)
                    .retrieve()
                    .body(AccountStatusResponse.class);
            if (response == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Identity trả về trạng thái tài khoản không hợp lệ.");
            }
            return "ACTIVE".equals(response.status());
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Không thể xác minh tài khoản với Identity.");
        }
    }

    private record AccountStatusResponse(String status) {}
}
