package com.doan1.identity.controller;

import com.doan1.identity.dto.request.LoginRequest;
import com.doan1.identity.dto.request.RegisterRequest;
import com.doan1.identity.dto.request.RefreshTokenRequest;
import com.doan1.identity.dto.response.AccountResponse;
import com.doan1.identity.dto.response.LoginResponse;
import com.doan1.identity.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/auth/register")
    public AccountResponse register(@Valid @RequestBody RegisterRequest request) {
        return auth.register(request);
    }

    @PostMapping("/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return auth.login(request);
    }

    @PostMapping("/auth/refresh")
    public LoginResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return auth.refresh(request.refreshToken());
    }

    @PostMapping("/auth/logout")
    public void logout(@Valid @RequestBody RefreshTokenRequest request) {
        auth.logout(request.refreshToken());
    }

    @GetMapping("/me")
    public AccountResponse currentAccount(HttpServletRequest request) {
        return auth.currentAccount(request);
    }
}
