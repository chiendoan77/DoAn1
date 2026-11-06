package com.doan1.identity.service;

import com.doan1.identity.entity.Account;
import com.doan1.identity.entity.RefreshToken;
import com.doan1.identity.repository.AccountRepository;
import com.doan1.identity.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokens;
    private final AccountRepository accounts;
    private final long ttlDays;
    private final SecureRandom random = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository refreshTokens,
            AccountRepository accounts,
            @Value("${security.jwt.refresh-ttl-days:30}") long ttlDays) {
        this.refreshTokens = refreshTokens;
        this.accounts = accounts;
        if (ttlDays < 1) {
            throw new IllegalArgumentException("JWT_REFRESH_TTL_DAYS must be positive.");
        }
        this.ttlDays = ttlDays;
    }

    @Transactional
    public IssuedToken issue(UUID accountId) {
        return create(accountId, UUID.randomUUID());
    }

    @Transactional(noRollbackFor = TokenReuseException.class)
    public IssuedToken rotate(String rawToken) {
        RefreshToken current = refreshTokens.findByTokenHashForUpdate(hash(rawToken))
                .orElseThrow(RefreshTokenService::invalidToken);
        if (current.isRevoked()) {
            refreshTokens.revokeActiveFamily(current.getFamilyId(), Instant.now());
            throw new TokenReuseException();
        }
        if (!current.getExpiresAt().isAfter(Instant.now())) {
            current.revoke();
            throw invalidToken();
        }

        Account account = accounts.findById(current.getAccountId())
                .filter(item -> item.getStatus() == com.doan1.identity.enums.AccountStatus.ACTIVE)
                .orElseThrow(RefreshTokenService::invalidToken);
        current.revoke();
        return create(account.getId(), current.getFamilyId());
    }

    @Transactional
    public Optional<UUID> revoke(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) return Optional.empty();
        RefreshToken token = refreshTokens.findByTokenHashForUpdate(hash(rawToken)).orElse(null);
        if (token == null) return Optional.empty();

        Instant now = Instant.now();
        boolean validSession = !token.isRevoked() && token.getExpiresAt().isAfter(now);
        refreshTokens.revokeActiveFamily(token.getFamilyId(), now);
        return validSession ? Optional.of(token.getAccountId()) : Optional.empty();
    }

    @Transactional
    public void revokeAndDeleteAllForAccount(UUID accountId) {
        refreshTokens.revokeActiveForAccount(accountId, Instant.now());
        refreshTokens.deleteAllForAccount(accountId);
    }

    private IssuedToken create(UUID accountId, UUID familyId) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        Instant expiresAt = Instant.now().plus(Duration.ofDays(ttlDays));
        refreshTokens.save(new RefreshToken(accountId, familyId, hash(rawToken), expiresAt));
        return new IssuedToken(accountId, rawToken, Math.multiplyExact(ttlDays, 24 * 60 * 60));
    }

    private static String hash(String token) {
        if (token == null || token.isBlank()) throw invalidToken();
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is required by the runtime.", exception);
        }
    }

    private static ResponseStatusException invalidToken() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token không hợp lệ hoặc đã hết hạn.");
    }

    public record IssuedToken(UUID accountId, String value, long expiresIn) {
    }

    public static final class TokenReuseException extends ResponseStatusException {
        public TokenReuseException() {
            super(HttpStatus.UNAUTHORIZED, "Refresh token đã được sử dụng; phiên đăng nhập đã bị thu hồi.");
        }
    }
}
