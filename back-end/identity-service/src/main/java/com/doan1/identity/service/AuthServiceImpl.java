package com.doan1.identity.service;

import com.doan1.identity.dto.request.LoginRequest;
import com.doan1.identity.dto.request.RegisterRequest;
import com.doan1.identity.dto.response.AccountResponse;
import com.doan1.identity.dto.response.LoginResponse;
import com.doan1.identity.entity.Account;
import com.doan1.identity.enums.Role;
import com.doan1.identity.enums.AccountStatus;
import com.doan1.identity.mapper.AccountMapper;
import com.doan1.identity.repository.AccountRepository;
import com.doan1.identity.security.JwtTokenService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    private final AccountRepository accounts;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtTokenService tokens;
    private final RefreshTokenService refreshTokens;

    public AuthServiceImpl(AccountRepository accounts, JwtTokenService tokens, RefreshTokenService refreshTokens) {
        this.accounts = accounts;
        this.tokens = tokens;
        this.refreshTokens = refreshTokens;
    }

    @Override
    @Transactional
    public AccountResponse register(RegisterRequest request) {
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
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email, số điện thoại hoặc tên đăng nhập đã được sử dụng.");
        }

        Account account = accounts.save(new Account(
                email,
                phone,
                username,
                request.fullName().trim(),
                passwordEncoder.encode(request.password()),
                role));
        return AccountMapper.toResponse(account);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String identifier = request.identifier().trim();
        Account account = identifier.contains("@")
                ? accounts.findByEmailIgnoreCase(identifier.toLowerCase(Locale.ROOT)).orElse(null)
                : accounts.findByPhoneNumber(normalizePhone(identifier)).orElse(null);

        if (account == null || !passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Thông tin đăng nhập không chính xác.");
        }
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản đã bị tạm ngưng.");
        }
        RefreshTokenService.IssuedToken refresh = refreshTokens.issue(account.getId());
        return response(account, refresh);
    }

    @Override
    public LoginResponse refresh(String refreshToken) {
        RefreshTokenService.IssuedToken rotated = refreshTokens.rotate(refreshToken);
        Account account = accounts.findById(rotated.accountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản không còn tồn tại."));
        return response(account, rotated);
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokens.revoke(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse currentAccount(HttpServletRequest request) {
        UUID accountId = tokens.authenticate(request).accountId();
        Account account = accounts.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản không còn tồn tại."));
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản đã bị tạm ngưng.");
        }
        return AccountMapper.toResponse(account);
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

    private LoginResponse response(Account account, RefreshTokenService.IssuedToken refresh) {
        return new LoginResponse(tokens.issue(account), tokens.ttlSeconds(),
                refresh.value(), refresh.expiresIn(), AccountMapper.toResponse(account));
    }

    private static String normalizePhone(String phoneNumber) {
        return phoneNumber.replaceAll("[\\s()-]", "");
    }
}
