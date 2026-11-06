package com.doan1.booking;

import com.doan1.booking.client.ProfileClient;
import com.doan1.booking.client.CatalogClient;
import com.doan1.booking.dto.response.MarketplaceCategoryResponse;
import com.doan1.booking.dto.response.PackageDetailsResponse;
import com.doan1.booking.dto.response.ProviderDetailsResponse;
import com.doan1.booking.entity.Booking;
import com.doan1.booking.repository.BookingRepository;
import com.doan1.booking.repository.BookingOutboxEventRepository;
import com.doan1.security.test.JwtTestKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.OffsetDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:booking;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=sa",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false",
        "app.outbox.enabled=false",
        "service.api-keys.profile=test-booking-profile-key",
        "service.api-keys.marketplace=internal-marketplace-booking-api-key-for-tests"
})
@AutoConfigureMockMvc
class BookingServiceApplicationTests {
    private static final String MARKETPLACE_BOOKING_API_KEY = "internal-marketplace-booking-api-key-for-tests";

    @DynamicPropertySource
    static void jwtKeys(DynamicPropertyRegistry registry) {
        JwtTestKeys.registerPublicKey(registry);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingRepository bookings;

    @Autowired
    private BookingOutboxEventRepository outboxEvents;

    @MockBean
    private ProfileClient profiles;

    @MockBean
    private CatalogClient catalog;

    @BeforeEach
    void resetState() {
        bookings.deleteAll();
        outboxEvents.deleteAll();
    }

    @Test
    void bookingHistoryIsPrivateAndProviderCanOnlyUpdateTheirAssignedBooking() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID providerAccountId = UUID.randomUUID();
        UUID providerProfileId = UUID.randomUUID();
        UUID packageId = UUID.randomUUID();
        when(catalog.getApprovedPackage(packageId))
                .thenReturn(new PackageDetailsResponse(
                        packageId, providerProfileId, "cleaning", "Gói tổng vệ sinh",
                        new BigDecimal("125000"), 90, "APPROVED"));
        when(profiles.getApprovedProvider("cleaning", providerProfileId))
                .thenReturn(new ProviderDetailsResponse(
                        providerProfileId,
                        providerAccountId,
                        "Nhà cung cấp",
                        List.of("cleaning"),
                        List.of(new MarketplaceCategoryResponse("cleaning", "Dọn dẹp nhà", "Dọn dẹp")),
                        "APPROVED"));

        String scheduledAt = OffsetDateTime.now().plusDays(2).withNano(0).toString();
        String result = mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + token(customerId, "CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "packageId": "%s",
                                  "scheduledAt": "%s",
                                  "address": "Quận 1, TP. Hồ Chí Minh",
                                  "description": "Dọn dẹp căn hộ."
                                }
                                """.formatted(packageId, scheduledAt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("REQUESTED")))
                .andExpect(jsonPath("$.providerName", is("Nhà cung cấp")))
                .andExpect(jsonPath("$.packageName", is("Gói tổng vệ sinh")))
                .andExpect(jsonPath("$.packagePrice", is(125000)))
                .andReturn().getResponse().getContentAsString();
        String bookingId = result.replaceAll("(?s).*\"id\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/api/bookings/mine")
                        .header("Authorization", "Bearer " + token(customerId, "CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(patch("/api/provider/bookings/" + bookingId + "/status")
                        .header("Authorization", "Bearer " + token(UUID.randomUUID(), "PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"ACCEPTED"}
                                """))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/provider/bookings/" + bookingId + "/status")
                        .header("Authorization", "Bearer " + token(providerAccountId, "PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"ACCEPTED"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ACCEPTED")));
        mockMvc.perform(get("/internal/bookings/" + bookingId + "/review-eligibility")
                        .header("X-Service-Api-Key", MARKETPLACE_BOOKING_API_KEY)
                        .param("reviewerAccountId", customerId.toString())
                        .param("reviewerRole", "CUSTOMER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible", is(false)));
        mockMvc.perform(patch("/api/provider/bookings/" + bookingId + "/status")
                        .header("Authorization", "Bearer " + token(providerAccountId, "PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"COMPLETED"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")));
        org.junit.jupiter.api.Assertions.assertEquals(1, outboxEvents.count());

        mockMvc.perform(get("/internal/bookings/" + bookingId + "/review-eligibility")
                        .header("X-Service-Api-Key", MARKETPLACE_BOOKING_API_KEY)
                        .param("reviewerAccountId", customerId.toString())
                        .param("reviewerRole", "CUSTOMER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible", is(true)))
                .andExpect(jsonPath("$.targetRole", is("PROVIDER")));
        mockMvc.perform(get("/internal/bookings/" + bookingId + "/review-eligibility")
                        .header("X-Service-Api-Key", MARKETPLACE_BOOKING_API_KEY)
                        .param("reviewerAccountId", providerAccountId.toString())
                        .param("reviewerRole", "PROVIDER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible", is(true)))
                .andExpect(jsonPath("$.targetRole", is("CUSTOMER")));
    }

    private static String token(UUID id, String role) {
        return JwtTestKeys.token(id, role, "Test User");
    }
}
