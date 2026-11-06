package com.doan1.profile.service;

import com.doan1.profile.dto.request.ProviderApplicationRequest;
import com.doan1.profile.dto.request.ProviderDecisionRequest;
import com.doan1.profile.dto.request.CustomerProfileRequest;
import com.doan1.profile.dto.response.CategoryResponse;
import com.doan1.profile.dto.response.InternalProviderResponse;
import com.doan1.profile.dto.response.ProviderEligibilityResponse;
import com.doan1.profile.dto.response.ProviderResponse;
import com.doan1.profile.client.IdentityAccountClient;
import com.doan1.profile.entity.ProviderProfile;
import com.doan1.profile.enums.ProviderStatus;
import com.doan1.profile.mapper.ProfileMapper;
import com.doan1.profile.repository.ProviderProfileRepository;
import com.doan1.profile.repository.CustomerProfileRepository;
import com.doan1.profile.entity.CustomerProfile;
import com.doan1.profile.repository.ProviderRatingReviewRepository;
import com.doan1.profile.entity.ProviderRatingReview;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ProfileService {
    private final ProviderProfileRepository providers;
    private final IdentityAccountClient identity;
    private final ProviderRatingReviewRepository ratingReviews;
    private final CustomerProfileRepository customerProfiles;

    public ProfileService(ProviderProfileRepository providers, IdentityAccountClient identity,
            ProviderRatingReviewRepository ratingReviews, CustomerProfileRepository customerProfiles) {
        this.providers = providers;
        this.identity = identity;
        this.ratingReviews = ratingReviews;
        this.customerProfiles = customerProfiles;
    }

    @Transactional(readOnly = true)
    public com.doan1.profile.dto.response.CustomerProfileResponse customerProfile(UUID accountId) {
        return customerProfiles.findById(accountId).map(ProfileService::customerResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hồ sơ khách hàng chưa được tạo."));
    }

    @Transactional
    public com.doan1.profile.dto.response.CustomerProfileResponse updateCustomerProfile(
            UUID accountId, CustomerProfileRequest input) {
        CustomerProfile profile = customerProfiles.findById(accountId)
                .orElseGet(() -> new CustomerProfile(accountId, input.displayName().trim(),
                        input.phoneNumber().trim(), input.defaultAddress().trim()));
        profile.update(input.displayName().trim(), input.phoneNumber().trim(), input.defaultAddress().trim(),
                input.avatarUrl() == null || input.avatarUrl().isBlank() ? null : input.avatarUrl().trim());
        return customerResponse(customerProfiles.save(profile));
    }

    private static com.doan1.profile.dto.response.CustomerProfileResponse customerResponse(CustomerProfile profile) {
        return new com.doan1.profile.dto.response.CustomerProfileResponse(
                profile.getDisplayName(), profile.getPhoneNumber(), profile.getDefaultAddress(), profile.getUpdatedAt(),
                profile.getAvatarUrl());
    }

    public List<CategoryResponse> categories() { return CategoryResponse.all(); }

    public List<ProviderResponse> providers(String categoryId, int limit) {
        requireCategory(categoryId);
        return providers.findPublicByCategory(normalizeCategory(categoryId), ProviderStatus.APPROVED,
                        PageRequest.of(0, Math.max(1, Math.min(limit, 10))))
                .stream().map(ProfileMapper::toResponse).toList();
    }

    public ProviderResponse provider(UUID providerId) {
        ProviderProfile profile = providers.findById(providerId)
                .filter(p -> p.getStatus() == ProviderStatus.APPROVED && !p.isSuspended())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhà cung cấp."));
        return ProfileMapper.toResponse(profile);
    }

    public ProviderResponse providerForCategory(String categoryId, UUID providerId) {
        requireCategory(categoryId);
        String normalizedCategoryId = normalizeCategory(categoryId);
        ProviderProfile profile = providers.findById(providerId)
                .filter(p -> p.getStatus() == ProviderStatus.APPROVED && !p.isSuspended())
                .filter(p -> p.getCategoryIds().contains(normalizedCategoryId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhà cung cấp."));
        return ProfileMapper.toResponse(profile);
    }

    public InternalProviderResponse internalProvider(UUID providerId, String categoryId) {
        requireCategory(categoryId);
        String normalizedCategoryId = normalizeCategory(categoryId);
        ProviderProfile profile = providers.findById(providerId)
                .filter(p -> p.getStatus() == ProviderStatus.APPROVED && !p.isSuspended())
                .filter(p -> p.getCategoryIds().contains(normalizedCategoryId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider is not approved for this category."));
        if (!identity.isActive(profile.getIdentityAccountId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider is not available.");
        }
        return ProfileMapper.toInternalResponse(profile);
    }

    @Transactional
    public ProviderResponse apply(UUID accountId, String fullName, ProviderApplicationRequest input) {
        requireActiveAccount(accountId);
        Set<String> normalizedCategoryIds = normalizeCategories(input.categoryIds());
        ProviderProfile profile = providers.findByIdentityAccountId(accountId).orElse(null);
        if (profile == null) {
            profile = providers.save(new ProviderProfile(accountId, fullName, normalizedCategoryIds,
                    input.address().trim(), input.imageUrl().trim(), input.description().trim(), ProviderStatus.PENDING));
        } else if (profile.getStatus() != ProviderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hồ sơ đã được gửi hoặc đã có kết quả duyệt.");
        }
        return ProfileMapper.toResponse(profile);
    }

    public ProviderResponse ownProfile(UUID accountId) {
        requireActiveAccount(accountId);
        return providers.findByIdentityAccountId(accountId).map(ProfileMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bạn chưa gửi hồ sơ nhà cung cấp."));
    }

    @Transactional
    public ProviderResponse updateOwnProfile(UUID accountId, ProviderApplicationRequest input) {
        requireActiveAccount(accountId);
        Set<String> normalizedCategoryIds = normalizeCategories(input.categoryIds());
        ProviderProfile profile = providers.findByIdentityAccountId(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bạn chưa gửi hồ sơ nhà cung cấp."));
        profile.updateProfile(normalizedCategoryIds, input.address().trim(),
                input.imageUrl().trim(), input.description().trim());
        return ProfileMapper.toResponse(profile);
    }

    public List<ProviderResponse> pendingProviders() {
        return providers.findByStatusOrderByCreatedAtAsc(ProviderStatus.PENDING)
                .stream().map(ProfileMapper::toResponse).toList();
    }

    @Transactional
    public ProviderResponse decideProvider(UUID id, ProviderDecisionRequest decision) {
        ProviderProfile profile = providers.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ."));
        if (profile.getStatus() != ProviderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hồ sơ này đã được xử lý.");
        }
        profile.setStatus(decision.approved() ? ProviderStatus.APPROVED : ProviderStatus.REJECTED);
        return ProfileMapper.toResponse(profile);
    }

    public ProviderEligibilityResponse eligibility(UUID accountId) {
        boolean active = identity.isActive(accountId);
        return providers.findByIdentityAccountId(accountId)
                .map(p -> new ProviderEligibilityResponse(
                        active && p.getStatus() == ProviderStatus.APPROVED && !p.isSuspended(), p.getId()))
                .orElse(new ProviderEligibilityResponse(false, null));
    }

    @Transactional
    public void addRatingReview(UUID reviewId, UUID providerId, int rating) {
        if (ratingReviews.existsById(reviewId)) return;
        ProviderProfile profile = providers.findByIdForUpdate(providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ nhà cung cấp."));
        if (ratingReviews.existsById(reviewId)) return;
        ratingReviews.save(new ProviderRatingReview(reviewId, providerId, rating));
        int previousCount = profile.getReviewCount();
        double average = ((profile.getRating() * previousCount) + rating) / (previousCount + 1);
        profile.setRatingAndReviewCount(average, previousCount + 1);
    }

    private void requireActiveAccount(UUID accountId) {
        if (!identity.isActive(accountId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản không hoạt động.");
        }
    }

    @Transactional
    public void setSuspended(UUID id, boolean suspended) {
        ProviderProfile profile = providers.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ."));
        profile.setSuspended(suspended);
    }

    private static void requireCategory(String categoryId) {
        if (categoryId == null || CategoryResponse.all().stream().noneMatch(c -> c.id().equals(normalizeCategory(categoryId)))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Danh mục dịch vụ không tồn tại.");
        }
    }

    private static String normalizeCategory(String categoryId) {
        return categoryId.toLowerCase(Locale.ROOT);
    }

    private static Set<String> normalizeCategories(List<String> categoryIds) {
        categoryIds.forEach(ProfileService::requireCategory);
        return categoryIds.stream().map(ProfileService::normalizeCategory).collect(java.util.stream.Collectors.toUnmodifiableSet());
    }
}
