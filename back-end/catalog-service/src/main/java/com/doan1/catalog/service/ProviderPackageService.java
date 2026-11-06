package com.doan1.catalog.service;

import com.doan1.catalog.client.ProviderEligibilityClient;
import com.doan1.catalog.dto.request.CreatePackageRequest;
import com.doan1.catalog.dto.request.UpdatePackageRequest;
import com.doan1.catalog.dto.response.PackageResponse;
import com.doan1.catalog.dto.response.PageResponse;
import com.doan1.catalog.enums.PackageStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProviderPackageService {
    private final ProviderEligibilityClient eligibility;
    private final PackageWorkflowService workflow;

    public ProviderPackageService(ProviderEligibilityClient eligibility, PackageWorkflowService workflow) {
        this.eligibility = eligibility;
        this.workflow = workflow;
    }

    public PackageResponse create(UUID accountId, CreatePackageRequest request) {
        UUID providerId = eligibility.requireEligibleProvider(accountId);
        return workflow.create(accountId, providerId, request);
    }

    public PageResponse<PackageResponse> list(
            UUID accountId, int page, int size, String sort, String direction) {
        return workflow.providerPackages(accountId, page, size, sort, direction);
    }

    public PackageResponse get(UUID accountId, UUID packageId) {
        return workflow.providerPackage(accountId, packageId);
    }

    public PackageResponse update(UUID accountId, UUID packageId, UpdatePackageRequest request) {
        PackageResponse existing = workflow.providerPackage(accountId, packageId);
        UUID providerId = existing.status() == PackageStatus.APPROVED
                ? eligibility.requireEligibleProvider(accountId)
                : null;
        return workflow.update(accountId, providerId, packageId, request);
    }

    public void delete(UUID accountId, UUID packageId, long expectedVersion) {
        workflow.delete(accountId, packageId, expectedVersion);
    }

    public PackageResponse submit(UUID accountId, UUID packageId, long expectedVersion) {
        UUID providerId = eligibility.requireEligibleProvider(accountId);
        return workflow.submit(accountId, providerId, packageId, expectedVersion);
    }
}
