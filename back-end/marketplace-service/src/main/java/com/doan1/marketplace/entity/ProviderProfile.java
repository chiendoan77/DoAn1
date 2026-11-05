package com.doan1.marketplace.entity;

import com.doan1.marketplace.enums.ProviderStatus;

import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "provider_profiles")
public class ProviderProfile {
    @Id
    private UUID id;

    @Column(name = "identity_account_id", nullable = false, unique = true)
    private UUID identityAccountId;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "provider_profile_categories", joinColumns = @JoinColumn(name = "provider_profile_id"))
    @Column(name = "category_id", nullable = false, length = 32)
    private Set<String> categoryIds;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(name = "image_url", nullable = false, length = 2048)
    private String imageUrl;

    @Column(nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ProviderStatus status;

    @Column(nullable = false)
    private double rating;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(name = "demo_fixture", nullable = false)
    private boolean demoFixture;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ProviderProfile() {
    }

    public ProviderProfile(
            UUID identityAccountId,
            String fullName,
            Set<String> categoryIds,
            String address,
            String imageUrl,
            String description,
            ProviderStatus status,
            double rating,
            int reviewCount,
            boolean demoFixture) {
        this.id = UUID.randomUUID();
        this.identityAccountId = identityAccountId;
        this.fullName = fullName;
        this.categoryIds = Set.copyOf(categoryIds);
        this.address = address;
        this.imageUrl = imageUrl;
        this.description = description;
        this.status = status;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.demoFixture = demoFixture;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getIdentityAccountId() {
        return identityAccountId;
    }

    public String getFullName() {
        return fullName;
    }

    public Set<String> getCategoryIds() {
        return categoryIds;
    }

    public String getAddress() {
        return address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public ProviderStatus getStatus() {
        return status;
    }

    public double getRating() {
        return rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public boolean isDemoFixture() {
        return demoFixture;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void updateProfile(Set<String> categoryIds, String address, String imageUrl, String description) {
        this.categoryIds = Set.copyOf(categoryIds);
        this.address = address;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public void setStatus(ProviderStatus status) {
        this.status = status;
    }

    public void setRatingAndReviewCount(double rating, int reviewCount) {
        this.rating = rating;
        this.reviewCount = reviewCount;
    }
}
