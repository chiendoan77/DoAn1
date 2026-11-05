package com.doan1.marketplace.controller;

import com.doan1.marketplace.dto.InternalProviderResponse;
import com.doan1.marketplace.model.ProviderProfile;
import com.doan1.marketplace.model.ProviderStatus;
import com.doan1.marketplace.repository.ProviderProfileRepository;

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
@RequestMapping("/internal/providers")
public class InternalProviderController {
    private final ProviderProfileRepository providers;
    private final byte[] serviceApiKey;

    public InternalProviderController(
            ProviderProfileRepository providers,
            @Value("${service.api-key:}") String serviceApiKey) {
        this.providers = providers;
        this.serviceApiKey = serviceApiKey.getBytes(StandardCharsets.UTF_8);
    }

    @GetMapping("/{providerId}")
    public InternalProviderResponse getApprovedProvider(
            HttpServletRequest request,
            @PathVariable UUID providerId,
            @RequestParam String categoryId) {
        String providedKey = request.getHeader("X-Service-Api-Key");
        if (serviceApiKey.length < 32) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Internal API key is not configured.");
        }
        if (providedKey == null || !MessageDigest.isEqual(
                serviceApiKey, providedKey.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid internal API key.");
        }
        ProviderProfile profile = providers.findById(providerId)
                .filter(candidate -> candidate.getStatus() == ProviderStatus.APPROVED)
                .filter(candidate -> candidate.getCategoryIds().contains(categoryId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider is not approved for this category."));
        return InternalProviderResponse.from(profile);
    }
}
