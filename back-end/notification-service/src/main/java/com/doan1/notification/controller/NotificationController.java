package com.doan1.notification.controller;

import com.doan1.notification.dto.NotificationResponse;
import com.doan1.notification.service.NotificationService;
import com.doan1.security.JwtPrincipalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final JwtPrincipalService principals;
    private final NotificationService notificationService;

    public NotificationController(JwtPrincipalService principals, NotificationService notificationService) {
        this.principals = principals;
        this.notificationService = notificationService;
    }

    @GetMapping("/mine")
    public List<NotificationResponse> mine(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        JwtPrincipalService.Principal principal = principals.authenticate(request);
        int safeSize = Math.min(Math.max(size, 1), 100);
        int safePage = Math.max(page, 0);
        if ("ADMIN".equals(principal.role())) {
            return notificationService.forAdmin(safePage, safeSize);
        }
        return notificationService.forUser(principal.accountId(), safePage, safeSize);
    }
}
