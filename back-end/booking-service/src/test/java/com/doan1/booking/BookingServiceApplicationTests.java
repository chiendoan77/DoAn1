package com.doan1.booking;

import com.doan1.booking.client.MarketplaceClient;
import com.doan1.booking.dto.response.MarketplaceCategoryResponse;
import com.doan1.booking.dto.response.ProviderDetailsResponse;
import com.doan1.booking.entity.Booking;
import com.doan1.booking.repository.BookingRepository;

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
import java.time.OffsetDateTime;
import java.util.Date;
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
        "security.jwt.secret=0123456789abcdef0123456789abcdef",
        "service.api-key=internal-service-api-key-for-tests"
})
@AutoConfigureMockMvc
class BookingServiceApplicationTests {
    private static final String JWT_SECRET = "0123456789abcdef0123456789abcdef";
    private static final String SERVICE_API_KEY = "internal-service-api-key-for-tests";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingRepository bookings;

    @MockBean
    private MarketplaceClient marketplace;

    @BeforeEach
    void resetState() {
        bookings.deleteAll();
    }

    @Test
    void bookingHistoryIsPrivateAndProviderCanOnlyUpdateTheirAssignedBooking() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID providerAccountId = UUID.randomUUID();
        UUID providerProfileId = UUID.randomUUID();
        when(marketplace.getApprovedProvider("cleaning", providerProfileId))
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
                                  "providerId": "%s",
                                  "categoryId": "cleaning",
                                  "scheduledAt": "%s",
                                  "address": "Quận 1, TP. Hồ Chí Minh",
                                  "description": "Dọn dẹp căn hộ."
                                }
                                """.formatted(providerProfileId, scheduledAt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("REQUESTED")))
                .andExpect(jsonPath("$.providerName", is("Nhà cung cấp")))
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
                        .header("X-Service-Api-Key", SERVICE_API_KEY)
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

        mockMvc.perform(get("/internal/bookings/" + bookingId + "/review-eligibility")
                        .header("X-Service-Api-Key", SERVICE_API_KEY)
                        .param("reviewerAccountId", customerId.toString())
                        .param("reviewerRole", "CUSTOMER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible", is(true)))
                .andExpect(jsonPath("$.targetRole", is("PROVIDER")));
        mockMvc.perform(get("/internal/bookings/" + bookingId + "/review-eligibility")
                        .header("X-Service-Api-Key", SERVICE_API_KEY)
                        .param("reviewerAccountId", providerAccountId.toString())
                        .param("reviewerRole", "PROVIDER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible", is(true)))
                .andExpect(jsonPath("$.targetRole", is("CUSTOMER")));
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
