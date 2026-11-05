package com.doan1.marketplace.controller;

import com.doan1.marketplace.dto.CategoryResponse;
import com.doan1.marketplace.dto.ProviderApplicationRequest;
import com.doan1.marketplace.dto.ProviderDecisionRequest;
import com.doan1.marketplace.dto.ProviderResponse;
import com.doan1.marketplace.model.ProviderProfile;
import com.doan1.marketplace.model.ProviderStatus;
import com.doan1.marketplace.repository.ProviderProfileRepository;
import com.doan1.marketplace.security.JwtPrincipalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class MarketplaceController {
    private final ProviderProfileRepository providers;
    private final JwtPrincipalService principals;

    public MarketplaceController(ProviderProfileRepository providers, JwtPrincipalService principals) {
        this.providers = providers;
        this.principals = principals;
    }

    @GetMapping("/services/categories")
    public List<CategoryResponse> categories() {
        return CategoryResponse.all();
    }

    @GetMapping("/services/{categoryId}/providers")
    public List<ProviderResponse> providers(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "10") int limit) {
        requireCategory(categoryId);
        int boundedLimit = Math.max(1, Math.min(limit, 10));
        return providers.findByCategoryIdAndStatus(
                        categoryId, ProviderStatus.APPROVED, PageRequest.of(0, boundedLimit))
                .stream()
                .map(ProviderResponse::from)
                .toList();
    }

    @GetMapping("/services/providers/{providerId}")
    public ProviderResponse provider(@PathVariable UUID providerId) {
        ProviderProfile provider = providers.findById(providerId)
                .filter(profile -> profile.getStatus() == ProviderStatus.APPROVED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhà cung cấp."));
        return ProviderResponse.from(provider);
    }

    @GetMapping("/services/{categoryId}/providers/{providerId}")
    public ProviderResponse providerForCategory(@PathVariable String categoryId, @PathVariable UUID providerId) {
        requireCategory(categoryId);
        ProviderProfile provider = providers.findById(providerId)
                .filter(profile -> profile.getStatus() == ProviderStatus.APPROVED)
                .filter(profile -> profile.getCategoryIds().contains(categoryId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhà cung cấp."));
        return ProviderResponse.from(provider);
    }

    @PostMapping("/provider/applications")
    public ProviderResponse apply(
            HttpServletRequest request,
            @Valid @RequestBody ProviderApplicationRequest application) {
        JwtPrincipalService.Principal principal = principals.requireRole(request, "PROVIDER");
        application.categoryIds().forEach(MarketplaceController::requireCategory);
        ProviderProfile profile = providers.findByIdentityAccountId(principal.accountId()).orElse(null);
        if (profile == null) {
            profile = new ProviderProfile(
                    principal.accountId(),
                    principal.fullName(),
                    java.util.Set.copyOf(application.categoryIds()),
                    application.address().trim(),
                    application.imageUrl().trim(),
                    application.description().trim(),
                    ProviderStatus.PENDING,
                    0,
                    0,
                    false);
        } else if (profile.getStatus() != ProviderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hồ sơ đã được gửi hoặc đã có kết quả duyệt.");
        }
        return ProviderResponse.from(providers.save(profile));
    }

    @GetMapping("/provider/profile")
    public ProviderResponse ownProfile(HttpServletRequest request) {
        JwtPrincipalService.Principal principal = principals.requireRole(request, "PROVIDER");
        ProviderProfile profile = providers.findByIdentityAccountId(principal.accountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bạn chưa gửi hồ sơ nhà cung cấp."));
        return ProviderResponse.from(profile);
    }

    @PutMapping("/provider/profile")
    public ProviderResponse updateOwnProfile(
            HttpServletRequest request,
            @Valid @RequestBody ProviderApplicationRequest updatedProfile) {
        JwtPrincipalService.Principal principal = principals.requireRole(request, "PROVIDER");
        updatedProfile.categoryIds().forEach(MarketplaceController::requireCategory);
        ProviderProfile profile = providers.findByIdentityAccountId(principal.accountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bạn chưa gửi hồ sơ nhà cung cấp."));

        profile.updateProfile(
                java.util.Set.copyOf(updatedProfile.categoryIds()),
                updatedProfile.address().trim(),
                updatedProfile.imageUrl().trim(),
                updatedProfile.description().trim());
        profile.setStatus(ProviderStatus.PENDING);
        return ProviderResponse.from(providers.save(profile));
    }

    @GetMapping("/admin/providers/pending")
    public List<ProviderResponse> pendingProviders(HttpServletRequest request) {
        principals.requireRole(request, "ADMIN");
        return providers.findByStatusOrderByCreatedAtAsc(ProviderStatus.PENDING)
                .stream()
                .map(ProviderResponse::from)
                .toList();
    }

    @PatchMapping("/admin/providers/{providerId}/approval")
    public ProviderResponse decideProvider(
            HttpServletRequest request,
            @PathVariable UUID providerId,
            @Valid @RequestBody ProviderDecisionRequest decision) {
        principals.requireRole(request, "ADMIN");
        ProviderProfile profile = providers.findById(providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ."));
        if (profile.getStatus() != ProviderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hồ sơ này đã được xử lý.");
        }
        profile.setStatus(decision.approved() ? ProviderStatus.APPROVED : ProviderStatus.REJECTED);
        return ProviderResponse.from(providers.save(profile));
    }

    private static void requireCategory(String categoryId) {
        boolean found = CategoryResponse.all().stream().anyMatch(category -> category.id().equals(categoryId.toLowerCase(Locale.ROOT)));
        if (!found) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Danh mục dịch vụ không tồn tại.");
        }
    }
}
