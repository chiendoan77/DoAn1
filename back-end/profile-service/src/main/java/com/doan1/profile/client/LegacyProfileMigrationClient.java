package com.doan1.profile.client;

import com.doan1.profile.entity.ProviderProfile;
import com.doan1.profile.enums.ProviderStatus;
import com.doan1.profile.repository.ProviderProfileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.profile-migration.enabled", havingValue = "true", matchIfMissing = true)
public class LegacyProfileMigrationClient implements CommandLineRunner {
    private final ProviderProfileRepository profiles;
    private final RestClient client;
    private final String migrationKey;

    public LegacyProfileMigrationClient(ProviderProfileRepository profiles, RestClient.Builder builder,
            @Value("${services.marketplace.url}") String marketplaceUrl,
            @Value("${service.profile-migration-api-key:}") String migrationKey) {
        this.profiles = profiles;
        this.client = builder.baseUrl(marketplaceUrl).build();
        this.migrationKey = migrationKey;
    }

    @Override
    public void run(String... args) {
        if (profiles.count() > 0) return;
        if (migrationKey.length() < 32) {
            throw new IllegalStateException("Profile migration API key is required while importing legacy profiles.");
        }
        try {
            LegacyProviderProfile[] result = client.get()
                    .uri("/internal/migration/provider-profiles")
                    .header("X-Service-Api-Key", migrationKey)
                    .retrieve()
                    .body(LegacyProviderProfile[].class);
            List<ProviderProfile> imported = result == null ? List.of() : Arrays.stream(result)
                    .map(p -> new ProviderProfile(p.id(), p.identityAccountId(), p.fullName(), p.categoryIds(),
                            p.address(), p.imageUrl(), p.description(), ProviderStatus.valueOf(p.status()),
                            p.rating(), p.reviewCount(), p.demoFixture(), p.createdAt()))
                    .toList();
            if (!imported.isEmpty()) profiles.saveAll(imported);
        } catch (RestClientException | IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Không thể nhập hồ sơ cũ từ Marketplace sang Profile.", exception);
        }
    }

    private record LegacyProviderProfile(java.util.UUID id, java.util.UUID identityAccountId, String fullName,
            java.util.Set<String> categoryIds, String address, String imageUrl, String description,
            String status, double rating, int reviewCount, boolean demoFixture, java.time.Instant createdAt) {
    }
}
