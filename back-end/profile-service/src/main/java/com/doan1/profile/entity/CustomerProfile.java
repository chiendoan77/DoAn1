package com.doan1.profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customer_profiles")
public class CustomerProfile {
    @Id
    @Column(name = "account_id")
    private UUID accountId;

    @Column(name = "display_name", nullable = false, length = 120)
    private String displayName;

    @Column(name = "phone_number", nullable = false, length = 24)
    private String phoneNumber;

    @Column(name = "default_address", nullable = false, length = 500)
    private String defaultAddress;

    @Column(name = "avatar_url", length = 2048)
    private String avatarUrl;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CustomerProfile() {
    }

    public CustomerProfile(UUID accountId, String displayName, String phoneNumber, String defaultAddress) {
        this.accountId = accountId;
        update(displayName, phoneNumber, defaultAddress);
    }

    public void update(String displayName, String phoneNumber, String defaultAddress) {
        update(displayName, phoneNumber, defaultAddress, avatarUrl);
    }

    public void update(String displayName, String phoneNumber, String defaultAddress, String avatarUrl) {
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.defaultAddress = defaultAddress;
        this.avatarUrl = avatarUrl;
        this.updatedAt = Instant.now();
    }

    public UUID getAccountId() { return accountId; }
    public String getDisplayName() { return displayName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getDefaultAddress() { return defaultAddress; }
    public String getAvatarUrl() { return avatarUrl; }
    public Instant getUpdatedAt() { return updatedAt; }
}
