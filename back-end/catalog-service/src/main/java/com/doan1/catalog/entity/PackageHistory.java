package com.doan1.catalog.entity;

import com.doan1.catalog.enums.HistoryAction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "package_history")
public class PackageHistory {
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "package_id", nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID packageId;

    @Column(name = "actor_id", nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID actorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private HistoryAction action;

    @Column(length = 1000)
    private String reason;

    @Column(name = "package_version", nullable = false)
    private long packageVersion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected PackageHistory() {
    }

    public PackageHistory(
            UUID packageId, UUID actorId, HistoryAction action, String reason, long packageVersion) {
        this.id = UUID.randomUUID();
        this.packageId = packageId;
        this.actorId = actorId;
        this.action = action;
        this.reason = reason;
        this.packageVersion = packageVersion;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getPackageId() {
        return packageId;
    }

    public UUID getActorId() {
        return actorId;
    }

    public HistoryAction getAction() {
        return action;
    }

    public String getReason() {
        return reason;
    }

    public long getPackageVersion() {
        return packageVersion;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
