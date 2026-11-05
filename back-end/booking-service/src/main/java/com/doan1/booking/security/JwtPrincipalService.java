package com.doan1.booking.security;

import com.doan1.booking.model.Booking;

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
import java.util.UUID;

@Service
public class JwtPrincipalService {
    private final SecretKey signingKey;

    public JwtPrincipalService(@Value("${security.jwt.secret}") String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET must be at least 32 bytes.");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public Principal requireRole(HttpServletRequest request, String expectedRole) {
        Principal principal = authenticate(request);
        if (!expectedRole.equals(principal.role())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện thao tác này.");
        }
        return principal;
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
                    claims.get("name", String.class),
                    claims.get("role", String.class));
        } catch (JwtException | IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Phiên đăng nhập không hợp lệ hoặc đã hết hạn.");
        }
    }

    public record Principal(UUID accountId, String fullName, String role) {
    }
}
