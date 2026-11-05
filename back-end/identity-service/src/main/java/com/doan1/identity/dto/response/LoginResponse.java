package com.doan1.identity.dto.response;

import com.doan1.identity.entity.Account;

public record LoginResponse(String accessToken, long expiresIn, AccountResponse account) {
}
