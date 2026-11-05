package com.doan1.identity.controller;

import com.doan1.identity.dto.AccountResponse;
import com.doan1.identity.dto.LoginRequest;
import com.doan1.identity.dto.LoginResponse;
import com.doan1.identity.dto.RegisterRequest;
import com.doan1.identity.model.Account;
import com.doan1.identity.model.Role;
import com.doan1.identity.repository.AccountRepository;
import com.doan1.identity.security.JwtTokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AccountRepository accounts;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtTokenService tokens;

    public AuthController(AccountRepository accounts, JwtTokenService tokens) {
        this.accounts = accounts;
        this.tokens = tokens;
    }

    @PostMapping("/auth/register")
    public AccountResponse register(@Valid @RequestBody RegisterRequest request) {
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu không được vượt quá 72 byte.");
        }
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String phone = normalizePhone(request.phoneNumber());
        String username = request.username().trim();
        Role role = parseRegistrationRole(request.role());

        if (accounts.existsByEmailIgnoreCase(email)
                || accounts.existsByPhoneNumber(phone)
                || accounts.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email, số điện thoại hoặc tên đăng nhập đã được sử dụng.");
        }

        Account account = accounts.save(new Account(
                email,
                phone,
                username,
                request.fullName().trim(),
                passwordEncoder.encode(request.password()),
                role));
        return AccountResponse.from(account);
    }

    @PostMapping("/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        String identifier = request.identifier().trim();
        Account account = identifier.contains("@")
                ? accounts.findByEmailIgnoreCase(identifier.toLowerCase(Locale.ROOT)).orElse(null)
                : accounts.findByPhoneNumber(normalizePhone(identifier)).orElse(null);

        if (account == null || !passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Thông tin đăng nhập không chính xác.");
        }
        return new LoginResponse(tokens.issue(account), tokens.ttlSeconds(), AccountResponse.from(account));
    }

    @GetMapping("/me")
    public AccountResponse currentAccount(HttpServletRequest request) {
        UUID accountId = tokens.authenticate(request).accountId();
        Account account = accounts.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản không còn tồn tại."));
        return AccountResponse.from(account);
    }

    private static Role parseRegistrationRole(String requestedRole) {
        try {
            Role role = Role.valueOf(requestedRole.trim().toUpperCase(Locale.ROOT));
            if (role == Role.ADMIN) {
                throw new IllegalArgumentException();
            }
            return role;
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vai trò đăng ký không hợp lệ.");
        }
    }

    private static String normalizePhone(String phoneNumber) {
        return phoneNumber.replaceAll("[\\s()-]", "");
    }

}
