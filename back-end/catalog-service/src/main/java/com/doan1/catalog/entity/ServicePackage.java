package com.doan1.catalog.entity;

import com.doan1.catalog.enums.PackageStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_packages")
public class ServicePackage {
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "provider_id", nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID providerId;

    @Column(name = "owner_account_id", nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID ownerAccountId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 0)
    private BigDecimal price;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "provider_phone", length = 32)
    private String providerPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private PackageStatus status;

    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;

    @Column(name = "has_been_submitted", nullable = false)
    private boolean hasBeenSubmitted;

    @Column(name = "has_been_rejected", nullable = false)
    private boolean hasBeenRejected;

    @Version
    @Column(nullable = false)
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ServicePackage() {
    }

    public ServicePackage(
            UUID ownerAccountId,
            UUID providerId,
            Category category,
            String name,
            String description,
            BigDecimal price,
            int durationMinutes,
            String providerPhone) {
        this.id = UUID.randomUUID();
        this.ownerAccountId = ownerAccountId;
        this.providerId = providerId;
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.providerPhone = normalizePhone(providerPhone);
        this.status = PackageStatus.DRAFT;
        this.version = 0;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void updateContent(
            Category category,
            String name,
            String description,
            BigDecimal price,
            int durationMinutes,
            String providerPhone) {
        PackageStatus previousStatus = this.status;
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.providerPhone = normalizePhone(providerPhone);
        this.rejectionReason = null;
        this.updatedAt = Instant.now();
        if (previousStatus == PackageStatus.APPROVED) {
            this.status = PackageStatus.PENDING_APPROVAL;
            this.hasBeenSubmitted = true;
        } else if (previousStatus == PackageStatus.REJECTED) {
            this.status = PackageStatus.DRAFT;
            this.hasBeenRejected = true;
        } else if (previousStatus == PackageStatus.INACTIVE) {
            this.status = PackageStatus.DRAFT;
        }
    }

    public void submit() {
        this.status = PackageStatus.PENDING_APPROVAL;
        this.hasBeenSubmitted = true;
        this.updatedAt = Instant.now();
    }

    public void approve() {
        this.status = PackageStatus.APPROVED;
        this.rejectionReason = null;
        this.updatedAt = Instant.now();
    }

    public void reject(String reason) {
        this.status = PackageStatus.REJECTED;
        this.rejectionReason = reason;
        this.hasBeenRejected = true;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = PackageStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public UUID getOwnerAccountId() {
        return ownerAccountId;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getProviderPhone() {
        return providerPhone;
    }

    public PackageStatus getStatus() {
        return status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public boolean hasBeenSubmitted() {
        return hasBeenSubmitted;
    }

    public boolean hasBeenRejected() {
        return hasBeenRejected;
    }

    public long getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private String normalizePhone(String phone) {
        return phone == null || phone.isBlank() ? null : phone.trim();
    }
}
