package com.doan1.identity.dto;

import com.doan1.identity.model.Account;

public record LoginResponse(String accessToken, long expiresIn, AccountResponse account) {
}
