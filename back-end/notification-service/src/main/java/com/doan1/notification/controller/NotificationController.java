package com.doan1.notification.controller;

import com.doan1.notification.entity.Notification;
import com.doan1.notification.dto.NotificationResponse;
import com.doan1.notification.repository.NotificationRepository;
import com.doan1.notification.security.JwtPrincipalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final JwtPrincipalService principals;
    private final NotificationRepository notifications;

    public NotificationController(JwtPrincipalService principals, NotificationRepository notifications) {
        this.principals = principals;
        this.notifications = notifications;
    }

    @GetMapping("/mine")
    public List<NotificationResponse> mine(HttpServletRequest request) {
        JwtPrincipalService.Principal principal = principals.authenticate(request);
        List<Notification> results;
        if ("ADMIN".equals(principal.role())) {
            results = notifications.findByRecipientRoleOrderByCreatedAtDesc("ADMIN");
        } else {
            results = notifications.findByRecipientIdOrderByCreatedAtDesc(principal.accountId());
        }
        return results.stream().map(n -> new NotificationResponse(
                n.getId(), n.getEventType(), n.getPackageId(), n.getRelatedId(), n.getRelatedType(),
                n.getMessage(), n.getCreatedAt())).toList();
    }
}
