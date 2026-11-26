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
}
