package com.doan1.gateway;

import com.doan1.security.test.JwtTestKeys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {})
@AutoConfigureWebTestClient
class ApiGatewayApplicationTests {
    @DynamicPropertySource
    static void jwtKeys(DynamicPropertyRegistry registry) {
        JwtTestKeys.registerPublicKey(registry);
    }

    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
    }

    @Test
    void rejectsProtectedRequestsWithoutJwt() {
        webTestClient.get()
                .uri("http://localhost:" + port + "/api/me")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void protectsCustomerProfileWithoutJwt() {
        webTestClient.get()
                .uri("http://localhost:" + port + "/api/customer/profile")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void rejectsMalformedJwtBeforeRouting() {
        webTestClient.get()
                .uri("http://localhost:" + port + "/api/admin/packages/pending")
                .header("Authorization", "Bearer invalid")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void rejectsAdminRouteForCustomerRole() {
        String token = JwtTestKeys.token(UUID.randomUUID(), "CUSTOMER", "Customer");
        webTestClient.get()
                .uri("http://localhost:" + port + "/api/admin/packages/pending")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void rateLimitsRepeatedAuthRequests() {
        for (int i = 0; i < 10; i++) {
            webTestClient.post()
                    .uri("http://localhost:" + port + "/api/auth/login")
                    .exchange();
        }
        webTestClient.post()
                .uri("http://localhost:" + port + "/api/auth/login")
                .exchange()
                .expectStatus().isEqualTo(429);
    }
}
