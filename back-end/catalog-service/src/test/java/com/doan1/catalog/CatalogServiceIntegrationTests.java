package com.doan1.catalog;

import com.doan1.catalog.client.ProviderEligibilityClient;
import com.doan1.catalog.entity.OutboxEvent;
import com.doan1.catalog.repository.OutboxEventRepository;
import com.doan1.catalog.repository.PackageHistoryRepository;
import com.doan1.catalog.repository.ServicePackageRepository;
import com.doan1.security.test.JwtTestKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:catalog;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=validate",
        "services.profile.url=http://localhost:9999",
        "services.profile.api-key=test-profile-key",
        "service.api-keys.booking=test-catalog-booking-key",
        "app.outbox.enabled=false"
})
@AutoConfigureMockMvc
class CatalogServiceIntegrationTests {
    @DynamicPropertySource
    static void jwtKeys(DynamicPropertyRegistry registry) {
        JwtTestKeys.registerPublicKey(registry);
    }

    private static final String CATEGORY_ID = "cleaning";
    private static final UUID PROVIDER_A_ACCOUNT = UUID.fromString("aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa");
    private static final UUID PROVIDER_B_ACCOUNT = UUID.fromString("bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb");
    private static final UUID PROVIDER_A_ID = UUID.fromString("aaaaaaaa-1111-4111-8111-aaaaaaaaaaaa");
    private static final UUID PROVIDER_B_ID = UUID.fromString("bbbbbbbb-1111-4111-8111-bbbbbbbbbbbb");
    private static final UUID ADMIN_ID = UUID.fromString("cccccccc-cccc-4ccc-8ccc-cccccccccccc");
    private static final UUID CUSTOMER_ID = UUID.fromString("dddddddd-dddd-4ddd-8ddd-dddddddddddd");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ServicePackageRepository packages;

    @Autowired
    private PackageHistoryRepository history;

    @Autowired
    private OutboxEventRepository outbox;

    @MockBean
    private ProviderEligibilityClient eligibility;

    @BeforeEach
    void cleanDatabase() {
        history.deleteAllInBatch();
        outbox.deleteAllInBatch();
        packages.deleteAllInBatch();
        when(eligibility.requireEligibleProvider(PROVIDER_A_ACCOUNT)).thenReturn(PROVIDER_A_ID);
        when(eligibility.requireEligibleProvider(PROVIDER_B_ACCOUNT)).thenReturn(PROVIDER_B_ID);
    }

    @Test
    void requiresVerifiedRoleAndEnforcesProviderOwnership() throws Exception {
        mvc.perform(post("/api/provider/packages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody(null)))
                .andExpect(status().isUnauthorized());

        mvc.perform(post("/api/provider/packages")
                        .header("Authorization", bearer(CUSTOMER_ID, "CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody(null)))
                .andExpect(status().isForbidden());

        String response = mvc.perform(post("/api/provider/packages")
                        .header("Authorization", bearer(PROVIDER_A_ACCOUNT, "PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody(PROVIDER_B_ID)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.providerId").value(PROVIDER_A_ID.toString()))
                .andReturn().getResponse().getContentAsString();
        String packageId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(response).get("id").asText();

        mvc.perform(get("/api/provider/packages/{id}", packageId)
                        .header("Authorization", bearer(PROVIDER_B_ACCOUNT, "PROVIDER")))
                .andExpect(status().isNotFound());

        mvc.perform(put("/api/provider/packages/{id}", packageId)
                        .header("Authorization", bearer(PROVIDER_B_ACCOUNT, "PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody(0)))
                .andExpect(status().isNotFound());

        mvc.perform(get("/api/admin/packages/pending")
                        .header("Authorization", bearer(PROVIDER_A_ACCOUNT, "PROVIDER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void failsClosedWhenProviderEligibilityCannotBeVerified() throws Exception {
        when(eligibility.requireEligibleProvider(PROVIDER_A_ACCOUNT))
                .thenThrow(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE));

        mvc.perform(post("/api/provider/packages")
                        .header("Authorization", bearer(PROVIDER_A_ACCOUNT, "PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody(null)))
                .andExpect(status().isServiceUnavailable());

        org.assertj.core.api.Assertions.assertThat(packages.count()).isZero();
    }

    @Test
    void requiresWholeVndPriceAndProviderPhone() throws Exception {
        mvc.perform(post("/api/provider/packages")
                        .header("Authorization", bearer(PROVIDER_A_ACCOUNT, "PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody(null).replace("125", "125.50")))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/provider/packages")
                        .header("Authorization", bearer(PROVIDER_A_ACCOUNT, "PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody(null).replace("\"providerPhone\":\"+84901234567\"", "\"providerPhone\":\"  \"")))
                .andExpect(status().isBadRequest());

        org.assertj.core.api.Assertions.assertThat(packages.count()).isZero();
    }

    @Test
    void exposesOpenApiAndSwaggerUi() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/provider/packages']").exists())
                .andExpect(jsonPath("$.paths['/api/admin/packages/{id}/reject']").exists());

        mvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void executesStatusFlowAndHidesPackagesUntilApproved() throws Exception {
        String packageId = createDraft();

        mvc.perform(post("/api/provider/packages/{id}/submit", packageId)
                        .header("Authorization", bearer(PROVIDER_A_ACCOUNT, "PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(versionBody(0)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"))
                .andExpect(jsonPath("$.version").value(1));

        mvc.perform(get("/api/catalog/packages/{id}", packageId))
                .andExpect(status().isNotFound());

        mvc.perform(post("/api/admin/packages/{id}/reject", packageId)
                        .header("Authorization", bearer(ADMIN_ID, "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("If-Match", "1")
                        .content("{}"))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/admin/packages/{id}/reject", packageId)
                        .header("Authorization", bearer(ADMIN_ID, "ADMIN"))
                        .header("If-Match", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"Please add package details\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.rejectionReason").value("Please add package details"));

        mvc.perform(put("/api/provider/packages/{id}", packageId)
                        .header("Authorization", bearer(PROVIDER_A_ACCOUNT, "PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody(2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.rejectionReason").value(nullValue()));

        mvc.perform(post("/api/provider/packages/{id}/submit", packageId)
                        .header("Authorization", bearer(PROVIDER_A_ACCOUNT, "PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(versionBody(3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"));

        mvc.perform(get("/api/admin/packages/pending")
                        .header("Authorization", bearer(ADMIN_ID, "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.number").value(0));

        mvc.perform(post("/api/admin/packages/{id}/approve", packageId)
                        .header("Authorization", bearer(ADMIN_ID, "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(versionBody(4)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.version").value(5));

        mvc.perform(get("/api/catalog/packages/{id}", packageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
        mvc.perform(get("/api/catalog/packages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(packageId))
                .andExpect(jsonPath("$.content[0].categoryId").value(CATEGORY_ID))
                .andExpect(jsonPath("$.content[0].providerPhone").value("+84907654321"))
                .andExpect(jsonPath("$.number").value(0));
        mvc.perform(get("/api/catalog/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("cleaning"))
                .andExpect(jsonPath("$[1].id").value("repair"));
        mvc.perform(get("/api/services/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("cleaning"))
                .andExpect(jsonPath("$[1].id").value("repair"));

        mvc.perform(post("/api/admin/packages/{id}/approve", packageId)
                        .header("Authorization", bearer(ADMIN_ID, "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(versionBody(4)))
                .andExpect(status().isConflict());

        mvc.perform(put("/api/provider/packages/{id}", packageId)
                        .header("Authorization", bearer(PROVIDER_A_ACCOUNT, "PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody(5)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"))
                .andExpect(jsonPath("$.version").value(6));

        mvc.perform(get("/api/catalog/packages/{id}", packageId))
                .andExpect(status().isNotFound());

        mvc.perform(get("/api/admin/packages/{id}/history", packageId)
                        .header("Authorization", bearer(ADMIN_ID, "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.action == 'APPROVED')].adminId").value(hasItem(ADMIN_ID.toString())))
                .andExpect(jsonPath("$[?(@.action == 'REJECTED')].reason")
                        .value(hasItem("Please add package details")));

        var eventTypes = outbox.findAll().stream().map(OutboxEvent::getEventType).toList();
        org.assertj.core.api.Assertions.assertThat(eventTypes)
                .contains("PackageSubmitted", "PackageRejected", "PackageResubmitted", "PackageApproved");
        OutboxEvent submitted = outbox.findAll().stream()
                .filter(event -> event.getEventType().equals("PackageSubmitted"))
                .findFirst().orElseThrow();
        org.assertj.core.api.Assertions.assertThat(submitted.getExchangeName()).isEqualTo("doan1.events");
        org.assertj.core.api.Assertions.assertThat(submitted.getRoutingKey())
                .isEqualTo("catalog.package.submitted");
        org.assertj.core.api.Assertions.assertThat(submitted.getPayload())
                .contains("\"eventId\"", "\"eventType\":\"PackageSubmitted\"", "\"packageId\"", "\"providerId\"",
                        "\"adminId\"", "\"reason\"", "\"occurredAt\"");
        mvc.perform(get("/api/catalog/packages/{id}", packageId))
                .andExpect(status().isNotFound());
    }

    private String createDraft() throws Exception {
        String response = mvc.perform(post("/api/provider/packages")
                        .header("Authorization", bearer(PROVIDER_A_ACCOUNT, "PROVIDER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody(null)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.providerPhone").value("+84901234567"))
                .andExpect(jsonPath("$.version").value(0))
                .andReturn().getResponse().getContentAsString();
        return com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(response).get("id").asText();
    }

    private String createBody(UUID forgedProviderId) {
        String providerField = forgedProviderId == null ? "" : ",\"providerId\":\"" + forgedProviderId + "\"";
        return """
                {
                  "categoryId":"%s",
                  "name":"Kitchen deep clean",
                  "description":"Detailed kitchen cleaning",
                  "price":125,
                  "durationMinutes":90,
                  "providerPhone":"+84901234567"%s
                }
                """.formatted(CATEGORY_ID, providerField);
    }

    private String updateBody(long expectedVersion) {
        return """
                {
                  "categoryId":"%s",
                  "name":"Kitchen deep clean plus",
                  "description":"Detailed kitchen cleaning and sanitizing",
                  "price":150,
                  "durationMinutes":100,
                  "providerPhone":"+84907654321",
                  "expectedVersion":%d,
                  "providerId":"%s"
                }
                """.formatted(CATEGORY_ID, expectedVersion, PROVIDER_B_ID);
    }

    private String versionBody(long expectedVersion) {
        return "{\"expectedVersion\":" + expectedVersion + "}";
    }

    private String bearer(UUID accountId, String role) {
        return "Bearer " + JwtTestKeys.token(accountId, role, "Test User");
    }
}
