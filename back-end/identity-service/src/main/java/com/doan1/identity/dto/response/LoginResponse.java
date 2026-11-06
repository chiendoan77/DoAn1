package com.doan1.identity.dto.response;

public record LoginResponse(
        String accessToken,
        long expiresIn,
        String refreshToken,
        long refreshExpiresIn,
        AccountResponse account) {
}
