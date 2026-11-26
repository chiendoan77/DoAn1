package com.doan1.notification.repository;

import com.doan1.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    boolean existsByEventId(UUID eventId);
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId);
    List<Notification> findByRecipientRoleOrderByCreatedAtDesc(String recipientRole);
    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId, Pageable pageable);
    Page<Notification> findByRecipientRoleOrderByCreatedAtDesc(String recipientRole, Pageable pageable);
}
