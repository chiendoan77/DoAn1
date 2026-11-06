package com.doan1.profile;

import com.doan1.profile.client.IdentityAccountClient;
import com.doan1.profile.entity.ProviderProfile;
import com.doan1.profile.enums.ProviderStatus;
import com.doan1.profile.repository.ProviderProfileRepository;
import com.doan1.profile.repository.CustomerProfileRepository;
import com.doan1.security.test.JwtTestKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:profile;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "app.profile-migration.enabled=false",
        "service.api-keys.booking=booking-internal-key-for-profile-tests",
        "service.api-keys.catalog=catalog-internal-key-for-profile-tests",
        "service.api-keys.review=review-internal-key-for-profile-tests"
})
@AutoConfigureMockMvc
class ProfileServiceApplicationTests {
    @DynamicPropertySource
    static void jwtKeys(DynamicPropertyRegistry registry) {
        JwtTestKeys.registerPublicKey(registry);
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private ProviderProfileRepository providers;
    @Autowired private CustomerProfileRepository customers;
    @MockBean private IdentityAccountClient identity;

    @BeforeEach
    void clearProfiles() {
        providers.deleteAll();
        customers.deleteAll();
    }

    @Test
    void providerCannotApplyWhenIdentityAccountIsSuspended() throws Exception {
        UUID accountId = UUID.randomUUID();
        when(identity.isActive(accountId)).thenReturn(false);
        mockMvc.perform(post("/api/provider/applications")
                        .header("Authorization", "Bearer " + token(accountId, "PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"categoryIds":["cleaning"],"address":"Hà Nội",
                                 "imageUrl":"https://example.com/image.jpg","description":"Dịch vụ"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void publicProviderLookupHidesUnapprovedAndSuspendedProfiles() throws Exception {
        UUID owner = UUID.randomUUID();
        ProviderProfile pending = providers.save(new ProviderProfile(owner, "Provider",
                Set.of("cleaning"), "Hà Nội", "https://example.com/image.jpg", "Dịch vụ",
                ProviderStatus.PENDING));
        mockMvc.perform(get("/api/services/cleaning/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").doesNotExist());

        pending.setStatus(ProviderStatus.APPROVED);
        pending.setSuspended(true);
        providers.save(pending);
        mockMvc.perform(get("/api/services/cleaning/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").doesNotExist());
    }

    @Test
    void catalogEligibilityRequiresActiveAccountAndApprovedProfile() throws Exception {
        UUID owner = UUID.randomUUID();
        ProviderProfile profile = providers.save(new ProviderProfile(owner, "Provider",
                Set.of("cleaning"), "Hà Nội", "https://example.com/image.jpg", "Dịch vụ",
                ProviderStatus.APPROVED));
        when(identity.isActive(owner)).thenReturn(true);
        mockMvc.perform(get("/internal/profiles/providers/" + owner + "/eligibility")
                        .header("X-Service-Api-Key", "catalog-internal-key-for-profile-tests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible", is(true)))
                .andExpect(jsonPath("$.providerId", is(profile.getId().toString())));
    }

    @Test
    void reviewRatingUpdateIsIdempotent() throws Exception {
        UUID owner = UUID.randomUUID();
        ProviderProfile profile = providers.save(new ProviderProfile(owner, "Provider",
                Set.of("cleaning"), "Hà Nội", "https://example.com/image.jpg", "Dịch vụ",
                ProviderStatus.APPROVED));
        UUID reviewId = UUID.randomUUID();
        String path = "/internal/profiles/providers/" + profile.getId() + "/rating-reviews";
        for (int attempt = 0; attempt < 2; attempt++) {
            mockMvc.perform(post(path)
                            .header("X-Service-Api-Key", "review-internal-key-for-profile-tests")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"reviewId":"%s","rating":5}
                                    """.formatted(reviewId)))
                    .andExpect(status().isOk());
        }
        ProviderProfile updated = providers.findById(profile.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(1, updated.getReviewCount());
        org.junit.jupiter.api.Assertions.assertEquals(5.0, updated.getRating());
    }

    @Test
    void customerCanCreateAndReadOwnProfile() throws Exception {
        UUID customerId = UUID.randomUUID();
        String authorization = "Bearer " + token(customerId, "CUSTOMER");

        mockMvc.perform(get("/api/customer/profile").header("Authorization", authorization))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/customer/profile")
                        .header("Authorization", authorization)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"displayName":"Khách hàng","phoneNumber":"0901234567",
                                 "defaultAddress":"Quận 1, TP. Hồ Chí Minh"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName", is("Khách hàng")))
                .andExpect(jsonPath("$.defaultAddress", is("Quận 1, TP. Hồ Chí Minh")));

        mockMvc.perform(get("/api/customer/profile").header("Authorization", authorization))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber", is("0901234567")));
    }

    private static String token(UUID id, String role) {
        return JwtTestKeys.token(id, role, "Provider");
    }
}
