package com.doan1.booking.client;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CatalogClientContractTests {
    @Test
    void requestsApprovedPackageUsingCatalogInternalContract() throws IOException {
        UUID packageId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/internal/catalog/packages/" + packageId, exchange -> {
            assertEquals("booking-catalog-contract-test-key",
                    exchange.getRequestHeaders().getFirst("X-Service-Api-Key"));
            byte[] body = """
                        {"id":"%s","providerId":"%s","categoryId":"cleaning",
                         "name":"Deep cleaning","price":125000,"durationMinutes":90,"status":"APPROVED"}
                        """.formatted(packageId, providerId).getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        try {
            String baseUrl = "http://localhost:" + server.getAddress().getPort();
            CatalogClient client = new CatalogClient(RestClient.builder(), baseUrl,
                    "booking-catalog-contract-test-key");

            var result = client.getApprovedPackage(packageId);

            assertEquals(packageId, result.id());
            assertEquals(providerId, result.providerId());
            assertEquals(125000, result.price().intValueExact());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void rejectsUnapprovedPackageResponseFromCatalog() throws IOException {
        UUID packageId = UUID.randomUUID();
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/internal/catalog/packages/" + packageId, exchange -> {
            byte[] body = """
                        {"id":"%s","providerId":"%s","categoryId":"cleaning",
                         "name":"Draft","price":125000,"durationMinutes":90,"status":"PENDING_APPROVAL"}
                        """.formatted(packageId, UUID.randomUUID()).getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        try {
            String baseUrl = "http://localhost:" + server.getAddress().getPort();
            CatalogClient client = new CatalogClient(RestClient.builder(), baseUrl, "booking-catalog-key");

            assertThrows(org.springframework.web.server.ResponseStatusException.class,
                    () -> client.getApprovedPackage(packageId));
        } finally {
            server.stop(0);
        }
    }
}
