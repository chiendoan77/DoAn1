package com.doan1.identity.controller;

import com.doan1.identity.dto.response.InternalAccountStatusResponse;
import com.doan1.identity.entity.Account;
import com.doan1.identity.enums.AccountStatus;
import com.doan1.identity.repository.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@RestController
public class InternalAccountController {
    private final AccountRepository accounts;
    private final byte[] profileApiKey;

    public InternalAccountController(AccountRepository accounts,
            @Value("${service.api-keys.profile:}") String profileApiKey) {
        this.accounts = accounts;
        this.profileApiKey = profileApiKey.getBytes(StandardCharsets.UTF_8);
    }

    @GetMapping("/internal/accounts/{accountId}/status")
    public InternalAccountStatusResponse getStatus(HttpServletRequest request, @PathVariable UUID accountId) {
        String provided = request.getHeader("X-Service-Api-Key");
        if (profileApiKey.length < 32) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Internal API key is not configured.");
        }
        if (provided == null || !MessageDigest.isEqual(profileApiKey, provided.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid internal API key.");
        }
        Account account = accounts.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found."));
        AccountStatus status = account.getStatus();
        return new InternalAccountStatusResponse(status);
    }
}
