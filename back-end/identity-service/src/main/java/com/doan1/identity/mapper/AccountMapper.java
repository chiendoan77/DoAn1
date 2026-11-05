package com.doan1.identity.mapper;

import com.doan1.identity.dto.response.AccountResponse;
import com.doan1.identity.entity.Account;

public final class AccountMapper {
    private AccountMapper() {
    }

    public static AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getEmail(),
                account.getPhoneNumber(),
                account.getUsername(),
                account.getFullName(),
                account.getRole());
    }
}
