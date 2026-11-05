package com.doan1.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ApiGatewayApplicationTests {
    private static final String ANGULAR_ORIGIN = "http://localhost:4200";

    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
    }

    @Test
    void allowsAngularPreflightRequests() {
        webTestClient.options()
                .uri("http://localhost:" + port + "/api/auth/register")
                .header(ORIGIN, ANGULAR_ORIGIN)
                .header(ACCESS_CONTROL_REQUEST_METHOD, "POST")
                .header(ACCESS_CONTROL_REQUEST_HEADERS, "content-type,authorization")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(ACCESS_CONTROL_ALLOW_ORIGIN, ANGULAR_ORIGIN)
                .expectHeader().value(ACCESS_CONTROL_ALLOW_METHODS,
                        value -> assertTrue(value.contains("POST")))
                .expectHeader().value(ACCESS_CONTROL_ALLOW_HEADERS,
                        value -> {
                            assertTrue(value.toLowerCase().contains("content-type"));
                            assertTrue(value.toLowerCase().contains("authorization"));
                        });
    }

    @Test
    void rejectsPreflightRequestsFromOtherOrigins() {
        webTestClient.options()
                .uri("http://localhost:" + port + "/api/auth/register")
                .header(ORIGIN, "http://localhost:4300")
                .header(ACCESS_CONTROL_REQUEST_METHOD, "POST")
                .exchange()
                .expectStatus().isForbidden();
    }
}
