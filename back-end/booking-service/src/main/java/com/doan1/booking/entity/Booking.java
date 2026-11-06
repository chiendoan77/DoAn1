package com.doan1.booking.entity;

import com.doan1.booking.enums.BookingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    private UUID id;

    @Column(name = "customer_account_id", nullable = false)
    private UUID customerAccountId;

    @Column(name = "provider_profile_id", nullable = false)
    private UUID providerProfileId;

    @Column(name = "provider_account_id", nullable = false)
    private UUID providerAccountId;

    @Column(name = "customer_name", nullable = false, length = 120)
    private String customerName;

    @Column(name = "provider_name", nullable = false, length = 120)
    private String providerName;

    @Column(name = "category_id", nullable = false, length = 32)
    private String categoryId;

    @Column(name = "category_name", nullable = false, length = 80)
    private String categoryName;

    @Column(name = "package_id")
    private UUID packageId;

    @Column(name = "package_name", length = 160)
    private String packageName;

    @Column(name = "package_price", precision = 10, scale = 0)
    private BigDecimal packagePrice;

    @Column(name = "package_duration_minutes")
    private Integer packageDurationMinutes;

    @Column(name = "scheduled_at", nullable = false)
    private OffsetDateTime scheduledAt;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private BookingStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Booking() {
    }

    public Booking(
            UUID customerAccountId,
            UUID providerProfileId,
            UUID providerAccountId,
            String customerName,
            String providerName,
            String categoryId,
            String categoryName,
            UUID packageId,
            String packageName,
            BigDecimal packagePrice,
            int packageDurationMinutes,
            OffsetDateTime scheduledAt,
            String address,
            String description) {
        this.id = UUID.randomUUID();
        this.customerAccountId = customerAccountId;
        this.providerProfileId = providerProfileId;
        this.providerAccountId = providerAccountId;
        this.customerName = customerName;
        this.providerName = providerName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.packageId = packageId;
        this.packageName = packageName;
        this.packagePrice = packagePrice;
        this.packageDurationMinutes = packageDurationMinutes;
        this.scheduledAt = scheduledAt;
        this.address = address;
        this.description = description;
        this.status = BookingStatus.REQUESTED;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerAccountId() {
        return customerAccountId;
    }

    public UUID getProviderProfileId() {
        return providerProfileId;
    }

    public UUID getProviderAccountId() {
        return providerAccountId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public UUID getPackageId() { return packageId; }
    public String getPackageName() { return packageName; }
    public BigDecimal getPackagePrice() { return packagePrice; }
    public Integer getPackageDurationMinutes() { return packageDurationMinutes; }

    public OffsetDateTime getScheduledAt() {
        return scheduledAt;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
