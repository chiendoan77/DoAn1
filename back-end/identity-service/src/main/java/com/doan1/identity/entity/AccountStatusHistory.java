package com.doan1.identity.entity;

import com.doan1.identity.enums.AccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "account_status_history")
public class AccountStatusHistory {
    @Id
    private UUID id;
    @Column(name = "account_id", nullable = false)
    private UUID accountId;
    @Column(name = "admin_id", nullable = false)
    private UUID adminId;
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false, length = 16)
    private AccountStatus previousStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 16)
    private AccountStatus newStatus;
    @Column(nullable = false, length = 500)
    private String reason;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected AccountStatusHistory() {}

    public AccountStatusHistory(UUID accountId, UUID adminId, AccountStatus previousStatus,
            AccountStatus newStatus, String reason) {
        this.id = UUID.randomUUID();
        this.accountId = accountId;
        this.adminId = adminId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.reason = reason;
        this.createdAt = Instant.now();
    }
}
