package com.doan1.marketplace;

import com.doan1.marketplace.client.BookingReviewEligibilityClient;
import com.doan1.marketplace.dto.response.ReviewEligibility;
import com.doan1.marketplace.entity.ProviderProfile;
import com.doan1.marketplace.enums.ProviderStatus;
import com.doan1.marketplace.repository.ProviderProfileRepository;
import com.doan1.marketplace.repository.ProviderReviewRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:marketplace;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=sa",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "security.jwt.secret=0123456789abcdef0123456789abcdef",
        "service.api-key=internal-service-api-key-for-tests",
        "app.seed-demo-providers=false"
})
@AutoConfigureMockMvc
class MarketplaceServiceApplicationTests {
    private static final String JWT_SECRET = "0123456789abcdef0123456789abcdef";
    private static final String SERVICE_API_KEY = "internal-service-api-key-for-tests";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProviderProfileRepository providers;

    @Autowired
    private ProviderReviewRepository reviews;

    @MockBean
    private BookingReviewEligibilityClient bookingEligibility;

    @BeforeEach
    void clearProviders() {
        reviews.deleteAll();
        providers.deleteAll();
    }

    @Test
    void sortsProvidersByRatingAndLimitsResultsToTen() throws Exception {
        for (int index = 0; index < 12; index++) {
            providers.save(new ProviderProfile(
                    UUID.randomUUID(),
                    "Provider " + index,
                    Set.of("cleaning"),
                    "Hà Nội",
                    "https://example.com/provider.jpg",
                    "Mô tả dịch vụ",
                    ProviderStatus.APPROVED,
                    index == 0 ? 5.0 : 4.0 - index / 10.0,
                    3,
                    false));
        }

        mockMvc.perform(get("/api/services/cleaning/providers").param("limit", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].fullName", is("Provider 0")))
                .andExpect(jsonPath("$[0].rating", is(5.0)));
    }

    @Test
    void providerApplicationStaysHiddenUntilAdminApprovesIt() throws Exception {
        String providerToken = token(UUID.randomUUID(), "PROVIDER");
        mockMvc.perform(post("/api/provider/applications")
                        .header("Authorization", "Bearer " + providerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "categoryIds": ["cleaning", "repair"],
                                  "address": "Quận 1, TP. Hồ Chí Minh",
                                  "imageUrl": "https://example.com/provider.jpg",
                                  "description": "Dịch vụ chăm sóc nhà."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.categories", hasSize(2)));

        mockMvc.perform(get("/api/services/cleaning/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        String pending = mockMvc.perform(get("/api/admin/providers/pending")
                        .header("Authorization", "Bearer " + token(UUID.randomUUID(), "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn().getResponse().getContentAsString();
        String providerId = pending.replaceAll("(?s).*?\"id\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(patch("/api/admin/providers/" + providerId + "/approval")
                        .header("Authorization", "Bearer " + token(UUID.randomUUID(), "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"approved":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));
        mockMvc.perform(get("/api/services/repair/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void providerCanUpdateOnlyOwnProfileAndChangesStayHiddenUntilAdminReapproves() throws Exception {
        UUID ownerId = UUID.randomUUID();
        ProviderProfile profile = providers.save(new ProviderProfile(
                ownerId,
                "Existing Provider",
                Set.of("cleaning"),
                "Địa chỉ cũ",
                "https://example.com/old-provider.jpg",
                "Mô tả cũ",
                ProviderStatus.APPROVED,
                4.5,
                8,
                false));
        String updatedProfile = """
                {
                  "categoryIds": ["repair"],
                  "address": "Địa chỉ mới",
                  "imageUrl": "https://example.com/new-provider.jpg",
                  "description": "Mô tả mới"
                }
                """;

        mockMvc.perform(put("/api/provider/profile")
                        .header("Authorization", "Bearer " + token(UUID.randomUUID(), "PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedProfile))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/provider/profile")
                        .header("Authorization", "Bearer " + token(ownerId, "CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedProfile))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/provider/profile")
                        .header("Authorization", "Bearer " + token(ownerId, "PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedProfile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(profile.getId().toString())))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.categoryIds[0]", is("repair")))
                .andExpect(jsonPath("$.address", is("Địa chỉ mới")))
                .andExpect(jsonPath("$.rating", is(4.5)))
                .andExpect(jsonPath("$.reviewCount", is(8)));

        mockMvc.perform(get("/api/services/cleaning/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        mockMvc.perform(get("/api/services/repair/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        mockMvc.perform(get("/api/services/providers/" + profile.getId()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/services/cleaning/providers/" + profile.getId()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/admin/providers/pending")
                        .header("Authorization", "Bearer " + token(UUID.randomUUID(), "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].address", is("Địa chỉ mới")))
                .andExpect(jsonPath("$[0].description", is("Mô tả mới")));

        mockMvc.perform(patch("/api/admin/providers/" + profile.getId() + "/approval")
                        .header("Authorization", "Bearer " + token(UUID.randomUUID(), "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"approved":true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));
        mockMvc.perform(get("/api/services/repair/providers/" + profile.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address", is("Địa chỉ mới")))
                .andExpect(jsonPath("$.description", is("Mô tả mới")));
    }

    @Test
    void internalProviderLookupRequiresServiceKeyAndOnlyReturnsApprovedMatchingProviders() throws Exception {
        ProviderProfile profile = providers.save(new ProviderProfile(
                UUID.randomUUID(),
                "Approved Provider",
                Set.of("cleaning"),
                "Hà Nội",
                "https://example.com/provider.jpg",
                "Mô tả dịch vụ",
                ProviderStatus.APPROVED,
                4.5,
                2,
                false));

        mockMvc.perform(get("/internal/providers/" + profile.getId())
                        .param("categoryId", "cleaning"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/internal/providers/" + profile.getId())
                        .header("X-Service-Api-Key", SERVICE_API_KEY)
                        .param("categoryId", "repair"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/internal/providers/" + profile.getId())
                        .header("X-Service-Api-Key", SERVICE_API_KEY)
                        .param("categoryId", "cleaning"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identityAccountId", is(profile.getIdentityAccountId().toString())))
                .andExpect(jsonPath("$.categories[0].id", is("cleaning")));
        mockMvc.perform(get("/api/services/cleaning/providers/" + profile.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identityAccountId").doesNotExist());
    }

    @Test
    void completedBookingReviewUpdatesProviderRatingAndCanOnlyBeSubmittedOnce() throws Exception {
        UUID providerAccountId = UUID.randomUUID();
        UUID providerProfileId = UUID.randomUUID();
        UUID reviewerId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();
        providers.save(new ProviderProfile(
                providerAccountId,
                "Provider reviewed",
                Set.of("cleaning"),
                "Hà Nội",
                "https://example.com/provider.jpg",
                "Mô tả dịch vụ",
                ProviderStatus.APPROVED,
                0,
                0,
                false));
        ProviderProfile provider = providers.findByIdentityAccountId(providerAccountId).orElseThrow();
        when(bookingEligibility.check(bookingId, reviewerId, "CUSTOMER"))
                .thenReturn(new ReviewEligibility(true, provider.getId(), "PROVIDER"));
        String customerToken = token(reviewerId, "CUSTOMER");

        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bookingId":"%s","rating":4,"comment":"Đúng giờ, làm việc tốt."}
                                """.formatted(bookingId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating", is(4)));

        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"bookingId":"%s","rating":5,"comment":"Đánh giá lần hai"}
                                """.formatted(bookingId)))
                .andExpect(status().isConflict());
        mockMvc.perform(get("/api/services/cleaning/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating", is(4.0)))
                .andExpect(jsonPath("$[0].reviewCount", is(1)));
    }

    private static String token(UUID id, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(id.toString())
                .claim("role", role)
                .claim("name", "Test User")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(600)))
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}
