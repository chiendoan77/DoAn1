package com.doan1.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutes {
    @Bean
    RouteLocator applicationRoutes(
            RouteLocatorBuilder builder,
            @Value("${services.identity.url:http://localhost:8081}") String identityUrl,
            @Value("${services.profile.url:http://localhost:8085}") String profileUrl,
            @Value("${services.catalog.url:http://localhost:8086}") String catalogUrl,
            @Value("${services.marketplace.url:http://localhost:8082}") String marketplaceUrl,
            @Value("${services.review.url:http://localhost:8088}") String reviewUrl,
            @Value("${services.booking.url:http://localhost:8083}") String bookingUrl,
            @Value("${services.payment.url:http://localhost:8084}") String paymentUrl,
            @Value("${services.notification.url:http://localhost:8087}") String notificationUrl) {
        return builder.routes()
                .route("identity", route -> route
                        .path("/api/auth/**", "/api/me/**", "/api/admin/users/**")
                        .uri(identityUrl))
                .route("profile", route -> route
                        .path("/api/services/*/providers/**", "/api/services/providers/**",
                                "/api/provider/profile/**", "/api/provider/applications/**",
                                "/api/admin/providers/**", "/api/customer/profile",
                                "/api/customer/profile/**")
                        .uri(profileUrl))
                .route("catalog-categories", route -> route
                        .path("/api/services/categories", "/api/catalog/**",
                                "/api/provider/packages/**", "/api/admin/packages/**", "/api/admin/categories/**")
                        .uri(catalogUrl))
                .route("notification", route -> route
                        .path("/api/notifications/**")
                        .uri(notificationUrl))
                .route("marketplace", route -> route
                        .path("/api/reviews/**")
                        .uri(reviewUrl))
                .route("booking", route -> route
                        .path("/api/bookings/**", "/api/provider/bookings/**", "/api/admin/bookings/**")
                        .uri(bookingUrl))
                .route("payment", route -> route
                        .path("/api/payments/**", "/api/admin/payments/**")
                        .uri(paymentUrl))
                .build();
    }
}
