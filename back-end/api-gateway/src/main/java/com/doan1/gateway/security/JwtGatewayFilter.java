package com.doan1.gateway.security;

import com.doan1.security.RsaJwtKeyLoader;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.PublicKey;
import java.util.Set;

@Component
public class JwtGatewayFilter implements GlobalFilter, Ordered {
    private static final Set<String> PROTECTED_PREFIXES = Set.of(
            "/api/me", "/api/provider/", "/api/admin/", "/api/customer/", "/api/bookings",
            "/api/reviews", "/api/notifications");
    private final PublicKey verificationKey;

    public JwtGatewayFilter(@Value("${security.jwt.public-key-base64}") String publicKey) {
        verificationKey = RsaJwtKeyLoader.publicKey(publicKey);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS || !requiresAuthentication(path)) {
            return chain.filter(exchange);
        }

        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ") || !isValid(header.substring(7))) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    private boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(verificationKey)
                    .requireIssuer("doan1-identity")
                    .requireAudience("doan1-platform")
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    private static boolean requiresAuthentication(String path) {
        return PROTECTED_PREFIXES.stream().anyMatch(prefix ->
                path.equals(prefix) || path.startsWith(prefix));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
