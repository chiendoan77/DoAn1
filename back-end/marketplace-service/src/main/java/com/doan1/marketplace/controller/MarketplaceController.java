package com.doan1.marketplace.controller;

import com.doan1.marketplace.dto.response.CategoryResponse;
import com.doan1.marketplace.dto.request.ProviderApplicationRequest;
import com.doan1.marketplace.dto.request.ProviderDecisionRequest;
import com.doan1.marketplace.dto.response.ProviderResponse;
import com.doan1.marketplace.security.JwtPrincipalService;
import com.doan1.marketplace.service.MarketplaceService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class MarketplaceController {
    private final MarketplaceService marketplace;
    private final JwtPrincipalService principals;

    public MarketplaceController(MarketplaceService marketplace, JwtPrincipalService principals) {
        this.marketplace = marketplace;
        this.principals = principals;
    }

    @GetMapping("/services/categories")
    public List<CategoryResponse> categories() {
        return marketplace.categories();
    }

    @GetMapping("/services/{categoryId}/providers")
    public List<ProviderResponse> providers(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "10") int limit) {
        return marketplace.providers(categoryId, limit);
    }

    @GetMapping("/services/providers/{providerId}")
    public ProviderResponse provider(@PathVariable UUID providerId) {
        return marketplace.provider(providerId);
    }

    @GetMapping("/services/{categoryId}/providers/{providerId}")
    public ProviderResponse providerForCategory(@PathVariable String categoryId, @PathVariable UUID providerId) {
        return marketplace.providerForCategory(categoryId, providerId);
    }

    @PostMapping("/provider/applications")
    public ProviderResponse apply(
            HttpServletRequest request,
            @Valid @RequestBody ProviderApplicationRequest application) {
        JwtPrincipalService.Principal principal = principals.requireRole(request, "PROVIDER");
        return marketplace.apply(principal.accountId(), principal.fullName(), application);
    }

    @GetMapping("/provider/profile")
    public ProviderResponse ownProfile(HttpServletRequest request) {
        JwtPrincipalService.Principal principal = principals.requireRole(request, "PROVIDER");
        return marketplace.ownProfile(principal.accountId());
    }

    @PutMapping("/provider/profile")
    public ProviderResponse updateOwnProfile(
            HttpServletRequest request,
            @Valid @RequestBody ProviderApplicationRequest updatedProfile) {
        JwtPrincipalService.Principal principal = principals.requireRole(request, "PROVIDER");
        return marketplace.updateOwnProfile(principal.accountId(), updatedProfile);
    }

    @GetMapping("/admin/providers/pending")
    public List<ProviderResponse> pendingProviders(HttpServletRequest request) {
        principals.requireRole(request, "ADMIN");
        return marketplace.pendingProviders();
    }

    @PatchMapping("/admin/providers/{providerId}/approval")
    public ProviderResponse decideProvider(
            HttpServletRequest request,
            @PathVariable UUID providerId,
            @Valid @RequestBody ProviderDecisionRequest decision) {
        principals.requireRole(request, "ADMIN");
        return marketplace.decideProvider(providerId, decision);
    }
}
