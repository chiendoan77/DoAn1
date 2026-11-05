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
            @Value("${services.marketplace.url:http://localhost:8082}") String marketplaceUrl,
            @Value("${services.booking.url:http://localhost:8083}") String bookingUrl,
            @Value("${services.payment.url:http://localhost:8084}") String paymentUrl) {
        return builder.routes()
                .route("identity", route -> route
                        .path("/api/auth/**", "/api/me/**", "/api/admin/users/**")
                        .uri(identityUrl))
                .route("marketplace", route -> route
                        .path("/api/services/**", "/api/reviews/**", "/api/provider/profile/**", "/api/provider/services/**",
                                "/api/provider/applications/**", "/api/admin/providers/**",
                                "/api/admin/categories/**")
                        .uri(marketplaceUrl))
                .route("booking", route -> route
                        .path("/api/bookings/**", "/api/provider/bookings/**", "/api/admin/bookings/**")
                        .uri(bookingUrl))
                .route("payment", route -> route
                        .path("/api/payments/**", "/api/admin/payments/**")
                        .uri(paymentUrl))
                .build();
    }
}
