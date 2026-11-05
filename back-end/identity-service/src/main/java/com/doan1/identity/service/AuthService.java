package com.doan1.identity.service;

import com.doan1.identity.dto.request.LoginRequest;
import com.doan1.identity.dto.request.RegisterRequest;
import com.doan1.identity.dto.response.AccountResponse;
import com.doan1.identity.dto.response.LoginResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    AccountResponse register(RegisterRequest input);

    LoginResponse login(LoginRequest input);

    AccountResponse currentAccount(HttpServletRequest request);
}
