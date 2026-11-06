package com.doan1.catalog.controller.internal;

import com.doan1.catalog.dto.response.PackageResponse;
import com.doan1.catalog.service.PackageWorkflowService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@RestController
@RequestMapping("/internal/catalog/packages")
public class InternalCatalogController {
    private final PackageWorkflowService workflow;
    private final String serviceApiKey;

    public InternalCatalogController(
            PackageWorkflowService workflow,
            @Value("${service.api-keys.booking:}") String serviceApiKey) {
        this.workflow = workflow;
        this.serviceApiKey = serviceApiKey;
    }

    @GetMapping("/{id}")
    public PackageResponse getApprovedPackage(
            @PathVariable UUID id,
            @RequestHeader(name = "X-Service-Api-Key", required = false) String suppliedKey) {
        if (serviceApiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Internal API key is not configured.");
        }
        if (suppliedKey == null || !MessageDigest.isEqual(
                serviceApiKey.getBytes(StandardCharsets.UTF_8), suppliedKey.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "A valid service API key is required.");
        }
        return workflow.publicPackage(id);
    }
}
