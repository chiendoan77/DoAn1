package com.doan1.marketplace.controller;

import com.doan1.marketplace.dto.response.InternalProviderResponse;
import com.doan1.marketplace.service.MarketplaceService;

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
    private final MarketplaceService marketplace;
    private final byte[] serviceApiKey;

    public InternalProviderController(
            MarketplaceService marketplace,
            @Value("${service.api-key:}") String serviceApiKey) {
        this.marketplace = marketplace;
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
        return marketplace.internalProvider(providerId, categoryId);
    }
}
