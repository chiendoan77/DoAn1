package com.doan1.identity.dto;

import com.doan1.identity.model.Account;
import com.doan1.identity.model.Role;

import java.util.UUID;

public record AccountResponse(UUID id, String email, String phoneNumber, String username, String fullName, Role role) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getEmail(),
                account.getPhoneNumber(),
                account.getUsername(),
                account.getFullName(),
                account.getRole());
    }
}
