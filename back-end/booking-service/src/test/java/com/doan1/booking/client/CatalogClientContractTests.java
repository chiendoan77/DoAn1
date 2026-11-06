package com.doan1.booking.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.GET;

class CatalogClientContractTests {
    @Test
    void requestsApprovedPackageUsingCatalogInternalContract() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        UUID packageId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        server.expect(requestTo("http://catalog/internal/catalog/packages/" + packageId))
                .andExpect(method(GET))
                .andExpect(header("X-Service-Api-Key", "booking-catalog-contract-test-key"))
                .andRespond(withSuccess("""
                        {"id":"%s","providerId":"%s","categoryId":"cleaning",
                         "name":"Deep cleaning","price":125000,"durationMinutes":90,"status":"APPROVED"}
                        """.formatted(packageId, providerId), MediaType.APPLICATION_JSON));
        CatalogClient client = new CatalogClient(builder, "http://catalog",
                "booking-catalog-contract-test-key");

        var result = client.getApprovedPackage(packageId);

        assertEquals(packageId, result.id());
        assertEquals(providerId, result.providerId());
        assertEquals(125000, result.price().intValueExact());
        server.verify();
    }

    @Test
    void rejectsUnapprovedPackageResponseFromCatalog() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        UUID packageId = UUID.randomUUID();
        server.expect(requestTo("http://catalog/internal/catalog/packages/" + packageId))
                .andRespond(withSuccess("""
                        {"id":"%s","providerId":"%s","categoryId":"cleaning",
                         "name":"Draft","price":125000,"durationMinutes":90,"status":"PENDING_APPROVAL"}
                        """.formatted(packageId, UUID.randomUUID()), MediaType.APPLICATION_JSON));
        CatalogClient client = new CatalogClient(builder, "http://catalog", "booking-catalog-key");

        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> client.getApprovedPackage(packageId));
        server.verify();
    }
}
