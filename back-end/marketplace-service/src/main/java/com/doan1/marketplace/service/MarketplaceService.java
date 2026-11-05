package com.doan1.marketplace.service;

import com.doan1.marketplace.dto.request.ProviderApplicationRequest;
import com.doan1.marketplace.dto.request.ProviderDecisionRequest;
import com.doan1.marketplace.dto.response.CategoryResponse;
import com.doan1.marketplace.dto.response.InternalProviderResponse;
import com.doan1.marketplace.dto.response.ProviderResponse;
import com.doan1.marketplace.entity.ProviderProfile;
import com.doan1.marketplace.enums.ProviderStatus;
import com.doan1.marketplace.mapper.MarketplaceMapper;
import com.doan1.marketplace.repository.ProviderProfileRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class MarketplaceService {
    private final ProviderProfileRepository providers;

    public MarketplaceService(ProviderProfileRepository providers) {
        this.providers = providers;
    }

    public List<CategoryResponse> categories() {
        return CategoryResponse.all();
    }

    public List<ProviderResponse> providers(String categoryId, int limit) {
        requireCategory(categoryId);
        int boundedLimit = Math.max(1, Math.min(limit, 10));
        return providers.findByCategoryIdAndStatus(
                        categoryId, ProviderStatus.APPROVED, PageRequest.of(0, boundedLimit))
                .stream()
                .map(MarketplaceMapper::toResponse)
                .toList();
    }

    public ProviderResponse provider(UUID providerId) {
        ProviderProfile provider = providers.findById(providerId)
                .filter(profile -> profile.getStatus() == ProviderStatus.APPROVED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhà cung cấp."));
        return MarketplaceMapper.toResponse(provider);
    }

    public ProviderResponse providerForCategory(String categoryId, UUID providerId) {
        requireCategory(categoryId);
        ProviderProfile provider = providers.findById(providerId)
                .filter(profile -> profile.getStatus() == ProviderStatus.APPROVED)
                .filter(profile -> profile.getCategoryIds().contains(categoryId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhà cung cấp."));
        return MarketplaceMapper.toResponse(provider);
    }

    public InternalProviderResponse internalProvider(UUID providerId, String categoryId) {
        ProviderProfile profile = providers.findById(providerId)
                .filter(candidate -> candidate.getStatus() == ProviderStatus.APPROVED)
                .filter(candidate -> candidate.getCategoryIds().contains(categoryId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Provider is not approved for this category."));
        return MarketplaceMapper.toInternalResponse(profile);
    }

    public ProviderResponse apply(UUID accountId, String fullName, ProviderApplicationRequest application) {
        application.categoryIds().forEach(MarketplaceService::requireCategory);
        ProviderProfile profile = providers.findByIdentityAccountId(accountId).orElse(null);
        if (profile == null) {
            profile = new ProviderProfile(
                    accountId,
                    fullName,
                    Set.copyOf(application.categoryIds()),
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
        return MarketplaceMapper.toResponse(providers.save(profile));
    }

    public ProviderResponse ownProfile(UUID accountId) {
        ProviderProfile profile = providers.findByIdentityAccountId(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bạn chưa gửi hồ sơ nhà cung cấp."));
        return MarketplaceMapper.toResponse(profile);
    }

    public ProviderResponse updateOwnProfile(UUID accountId, ProviderApplicationRequest updatedProfile) {
        updatedProfile.categoryIds().forEach(MarketplaceService::requireCategory);
        ProviderProfile profile = providers.findByIdentityAccountId(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bạn chưa gửi hồ sơ nhà cung cấp."));

        profile.updateProfile(
                Set.copyOf(updatedProfile.categoryIds()),
                updatedProfile.address().trim(),
                updatedProfile.imageUrl().trim(),
                updatedProfile.description().trim());
        profile.setStatus(ProviderStatus.PENDING);
        return MarketplaceMapper.toResponse(providers.save(profile));
    }

    public List<ProviderResponse> pendingProviders() {
        return providers.findByStatusOrderByCreatedAtAsc(ProviderStatus.PENDING)
                .stream()
                .map(MarketplaceMapper::toResponse)
                .toList();
    }

    public ProviderResponse decideProvider(UUID providerId, ProviderDecisionRequest decision) {
        ProviderProfile profile = providers.findById(providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ."));
        if (profile.getStatus() != ProviderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hồ sơ này đã được xử lý.");
        }
        profile.setStatus(decision.approved() ? ProviderStatus.APPROVED : ProviderStatus.REJECTED);
        return MarketplaceMapper.toResponse(providers.save(profile));
    }

    private static void requireCategory(String categoryId) {
        boolean found = CategoryResponse.all().stream()
                .anyMatch(category -> category.id().equals(categoryId.toLowerCase(Locale.ROOT)));
        if (!found) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Danh mục dịch vụ không tồn tại.");
        }
    }
}
