package com.doan1.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    private UUID id;
    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;
    @Column(name = "recipient_id")
    private UUID recipientId;
    @Column(name = "recipient_role", nullable = false, length = 16)
    private String recipientRole;
    @Column(name = "event_type", nullable = false, length = 40)
    private String eventType;
    @Column(name = "package_id")
    private UUID packageId;
    @Column(name = "related_id")
    private UUID relatedId;
    @Column(name = "related_type", length = 16)
    private String relatedType;
    @Column(nullable = false, length = 1000)
    private String message;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Notification() {}

    public Notification(UUID eventId, UUID recipientId, String recipientRole, String eventType,
            UUID packageId, String message) {
        this.id = UUID.randomUUID();
        this.eventId = eventId;
        this.recipientId = recipientId;
        this.recipientRole = recipientRole;
        this.eventType = eventType;
        this.packageId = packageId;
        this.relatedId = packageId;
        this.relatedType = "PACKAGE";
        this.message = message;
        this.createdAt = Instant.now();
    }

    public Notification(UUID eventId, UUID recipientId, String recipientRole, String eventType,
            UUID relatedId, String relatedType, String message) {
        this.id = UUID.randomUUID();
        this.eventId = eventId;
        this.recipientId = recipientId;
        this.recipientRole = recipientRole;
        this.eventType = eventType;
        this.relatedId = relatedId;
        this.relatedType = relatedType;
        this.message = message;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getEventId() { return eventId; }
    public UUID getRecipientId() { return recipientId; }
    public String getRecipientRole() { return recipientRole; }
    public String getEventType() { return eventType; }
    public UUID getPackageId() { return packageId; }
    public UUID getRelatedId() { return relatedId; }
    public String getRelatedType() { return relatedType; }
    public String getMessage() { return message; }
    public Instant getCreatedAt() { return createdAt; }
}
