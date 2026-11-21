package com.doan1.security.test;

import io.jsonwebtoken.Jwts;
import org.springframework.test.context.DynamicPropertyRegistry;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public final class JwtTestKeys {
    private static final KeyPair KEY_PAIR = generate();

    private JwtTestKeys() {
    }

    public static void registerPublicKey(DynamicPropertyRegistry registry) {
        registry.add("security.jwt.public-key-base64",
                () -> Base64.getEncoder().encodeToString(KEY_PAIR.getPublic().getEncoded()));
    }

    public static void registerKeyPair(DynamicPropertyRegistry registry) {
        registerPublicKey(registry);
        registry.add("security.jwt.private-key-base64",
                () -> Base64.getEncoder().encodeToString(KEY_PAIR.getPrivate().getEncoded()));
    }

    public static String token(UUID accountId, String role, String fullName) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(accountId.toString())
                .issuer("doan1-identity")
                .audience().add("doan1-platform").and()
                .claim("role", role)
                .claim("name", fullName)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(600)))
                .signWith(KEY_PAIR.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    private static KeyPair generate() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }
}
