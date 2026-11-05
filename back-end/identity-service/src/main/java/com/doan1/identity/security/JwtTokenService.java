package com.doan1.identity.security;

import com.doan1.identity.model.Account;
import com.doan1.identity.model.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenService {
    private final SecretKey signingKey;
    private final long ttlSeconds;

    public JwtTokenService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.ttl-seconds:3600}") long ttlSeconds) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET must be at least 32 bytes.");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.ttlSeconds = ttlSeconds;
    }

    public String issue(Account account) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(account.getId().toString())
                .claim("role", account.getRole().name())
                .claim("name", account.getFullName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(signingKey)
                .compact();
    }

    public long ttlSeconds() {
        return ttlSeconds;
    }

    public Principal authenticate(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập.");
        }
        try {
            Claims claims = Jwts.parser().verifyWith(signingKey).build()
                    .parseSignedClaims(header.substring(7)).getPayload();
            return new Principal(
                    UUID.fromString(claims.getSubject()),
                    Role.valueOf(claims.get("role", String.class)),
                    claims.get("name", String.class));
        } catch (JwtException | IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Phiên đăng nhập không hợp lệ hoặc đã hết hạn.");
        }
    }

    public record Principal(UUID accountId, Role role, String fullName) {
    }
}
