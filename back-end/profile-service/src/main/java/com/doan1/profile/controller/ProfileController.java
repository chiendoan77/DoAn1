package com.doan1.profile.controller;

import com.doan1.profile.dto.request.ProviderApplicationRequest;
import com.doan1.profile.dto.request.ProviderDecisionRequest;
import com.doan1.profile.dto.request.CustomerProfileRequest;
import com.doan1.profile.dto.response.CategoryResponse;
import com.doan1.profile.dto.response.ProviderResponse;
import com.doan1.profile.security.JwtPrincipalService;
import com.doan1.profile.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ProfileController {
    private final ProfileService profiles;
    private final JwtPrincipalService principals;

    public ProfileController(ProfileService profiles, JwtPrincipalService principals) {
        this.profiles = profiles;
        this.principals = principals;
    }

    @GetMapping("/customer/profile")
    public com.doan1.profile.dto.response.CustomerProfileResponse ownCustomerProfile(HttpServletRequest request) {
        return profiles.customerProfile(principals.requireRole(request, "CUSTOMER").accountId());
    }

    @PutMapping("/customer/profile")
    public com.doan1.profile.dto.response.CustomerProfileResponse updateCustomerProfile(
            HttpServletRequest request, @Valid @RequestBody CustomerProfileRequest input) {
        return profiles.updateCustomerProfile(
                principals.requireRole(request, "CUSTOMER").accountId(), input);
    }

    @GetMapping("/services/{categoryId}/providers")
    public List<ProviderResponse> providers(@PathVariable String categoryId,
            @RequestParam(defaultValue = "10") int limit) {
        return profiles.providers(categoryId, limit);
    }

    @GetMapping("/services/providers/{providerId}")
    public ProviderResponse provider(@PathVariable UUID providerId) { return profiles.provider(providerId); }

    @GetMapping("/services/{categoryId}/providers/{providerId}")
    public ProviderResponse providerForCategory(@PathVariable String categoryId, @PathVariable UUID providerId) {
        return profiles.providerForCategory(categoryId, providerId);
    }

    @PostMapping("/provider/applications")
    public ProviderResponse apply(HttpServletRequest request, @Valid @RequestBody ProviderApplicationRequest input) {
        JwtPrincipalService.Principal principal = principals.requireRole(request, "PROVIDER");
        return profiles.apply(principal.accountId(), principal.fullName(), input);
    }

    @GetMapping("/provider/profile")
    public ProviderResponse ownProfile(HttpServletRequest request) {
        return profiles.ownProfile(principals.requireRole(request, "PROVIDER").accountId());
    }

    @PutMapping("/provider/profile")
    public ProviderResponse updateOwnProfile(HttpServletRequest request,
            @Valid @RequestBody ProviderApplicationRequest input) {
        return profiles.updateOwnProfile(principals.requireRole(request, "PROVIDER").accountId(), input);
    }

    @GetMapping("/admin/providers/pending")
    public List<ProviderResponse> pendingProviders(HttpServletRequest request) {
        principals.requireRole(request, "ADMIN");
        return profiles.pendingProviders();
    }

    @PatchMapping("/admin/providers/{providerId}/approval")
    public ProviderResponse decideProvider(HttpServletRequest request, @PathVariable UUID providerId,
            @Valid @RequestBody ProviderDecisionRequest decision) {
        principals.requireRole(request, "ADMIN");
        return profiles.decideProvider(providerId, decision);
    }

    @PatchMapping("/admin/providers/{providerId}/suspension")
    public void suspendProvider(HttpServletRequest request, @PathVariable UUID providerId,
            @RequestBody SuspensionRequest input) {
        principals.requireRole(request, "ADMIN");
        profiles.setSuspended(providerId, input.suspended());
    }

    public record SuspensionRequest(boolean suspended) {}
}
