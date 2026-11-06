package com.doan1.profile.security;

import com.doan1.security.RsaJwtKeyLoader;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.PublicKey;
import java.util.UUID;

@Service
public class JwtPrincipalService {
    private final PublicKey verificationKey;

    public JwtPrincipalService(@Value("${security.jwt.public-key-base64}") String publicKey) {
        verificationKey = RsaJwtKeyLoader.publicKey(publicKey);
    }

    public Principal requireRole(HttpServletRequest request, String role) {
        Principal principal = authenticate(request);
        if (!role.equals(principal.role())) {
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
            Claims claims = Jwts.parser().verifyWith(verificationKey)
                    .requireIssuer("doan1-identity")
                    .requireAudience("doan1-platform")
                    .build()
                    .parseSignedClaims(header.substring(7)).getPayload();
            return new Principal(UUID.fromString(claims.getSubject()), claims.get("name", String.class),
                    claims.get("role", String.class));
        } catch (JwtException | IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Phiên đăng nhập không hợp lệ hoặc đã hết hạn.");
        }
    }

    public record Principal(UUID accountId, String fullName, String role) {}
}
