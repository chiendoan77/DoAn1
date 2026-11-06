package com.doan1.marketplace.controller;

import com.doan1.marketplace.entity.ProviderProfile;
import com.doan1.marketplace.repository.ProviderProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/internal/migration")
public class InternalProfileMigrationController {
    private final ProviderProfileRepository providers;
    private final byte[] migrationKey;

    public InternalProfileMigrationController(ProviderProfileRepository providers,
            @Value("${service.profile-migration-api-key:}") String migrationKey) {
        this.providers = providers;
        this.migrationKey = migrationKey.getBytes(StandardCharsets.UTF_8);
    }

    @GetMapping("/provider-profiles")
    public List<LegacyProviderProfile> exportProviderProfiles(HttpServletRequest request) {
        String provided = request.getHeader("X-Service-Api-Key");
        if (migrationKey.length < 32) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Profile migration key is not configured.");
        }
        if (provided == null || !MessageDigest.isEqual(migrationKey, provided.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid profile migration key.");
        }
        return providers.findAll().stream().map(profile -> new LegacyProviderProfile(
                profile.getId(), profile.getIdentityAccountId(), profile.getFullName(), profile.getCategoryIds(),
                profile.getAddress(), profile.getImageUrl(), profile.getDescription(), profile.getStatus().name(),
                profile.getRating(), profile.getReviewCount(), profile.isDemoFixture(), profile.getCreatedAt())).toList();
    }

    public record LegacyProviderProfile(UUID id, UUID identityAccountId, String fullName, Set<String> categoryIds,
            String address, String imageUrl, String description, String status, double rating,
            int reviewCount, boolean demoFixture, Instant createdAt) {
    }
}
