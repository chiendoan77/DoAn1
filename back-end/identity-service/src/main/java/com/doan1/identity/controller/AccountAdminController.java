package com.doan1.identity.controller;

import com.doan1.identity.dto.request.AccountStatusRequest;
import com.doan1.identity.entity.Account;
import com.doan1.identity.entity.AccountStatusHistory;
import com.doan1.identity.enums.AccountStatus;
import com.doan1.identity.repository.AccountRepository;
import com.doan1.identity.repository.AccountStatusHistoryRepository;
import com.doan1.identity.security.JwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
public class AccountAdminController {
    private final AccountRepository accounts;
    private final AccountStatusHistoryRepository statusHistory;
    private final JwtTokenService tokens;

    public AccountAdminController(AccountRepository accounts, AccountStatusHistoryRepository statusHistory,
            JwtTokenService tokens) {
        this.accounts = accounts;
        this.statusHistory = statusHistory;
        this.tokens = tokens;
    }

    @PatchMapping("/{accountId}/status")
    @Transactional
    public void changeStatus(HttpServletRequest request, @PathVariable UUID accountId,
            @Valid @RequestBody AccountStatusRequest input) {
        var admin = tokens.authenticate(request);
        if (admin.role() != com.doan1.identity.enums.Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện thao tác này.");
        }
        Account account = accounts.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản."));
        AccountStatus previousStatus = account.getStatus();
        if (previousStatus == input.status()) return;
        account.setStatus(input.status());
        statusHistory.save(new AccountStatusHistory(
                accountId, admin.accountId(), previousStatus, input.status(), input.reason().trim()));
    }
}
