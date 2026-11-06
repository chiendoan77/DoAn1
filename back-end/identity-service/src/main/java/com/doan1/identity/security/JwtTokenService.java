package com.doan1.identity.security;

import com.doan1.identity.entity.Account;
import com.doan1.identity.enums.Role;
import com.doan1.security.RsaJwtKeyLoader;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenService {
    private final PrivateKey signingKey;
    private final PublicKey verificationKey;
    private final long ttlSeconds;

    public JwtTokenService(
            @Value("${security.jwt.private-key-base64}") String privateKey,
            @Value("${security.jwt.public-key-base64}") String publicKey,
            @Value("${security.jwt.ttl-seconds:3600}") long ttlSeconds) {
        this.signingKey = RsaJwtKeyLoader.privateKey(privateKey);
        this.verificationKey = RsaJwtKeyLoader.publicKey(publicKey);
        this.ttlSeconds = ttlSeconds;
    }

    public String issue(Account account) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(account.getId().toString())
                .issuer("doan1-identity")
                .audience().add("doan1-platform").and()
                .claim("role", account.getRole().name())
                .claim("name", account.getFullName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(signingKey, Jwts.SIG.RS256)
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
            Claims claims = Jwts.parser().verifyWith(verificationKey)
                    .requireIssuer("doan1-identity")
                    .requireAudience("doan1-platform")
                    .build()
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
