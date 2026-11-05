package com.doan1.identity.dto.request;

import com.doan1.identity.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Email không được để trống.")
        @Email(message = "Email không đúng định dạng.")
        @Size(max = 320, message = "Email không được vượt quá 320 ký tự.")
        String email,
        @NotBlank(message = "Số điện thoại không được để trống.")
        @Pattern(regexp = "^\\+?[0-9][0-9 ()-]{6,19}$", message = "Số điện thoại không đúng định dạng.")
        String phoneNumber,
        @NotBlank(message = "Tên đăng nhập không được để trống.")
        @Pattern(regexp = "^[a-zA-Z0-9._-]{3,40}$", message = "Tên đăng nhập phải có 3-40 ký tự gồm chữ, số, dấu chấm, gạch dưới hoặc gạch ngang.")
        String username,
        @NotBlank(message = "Họ và tên không được để trống.")
        @Size(max = 120, message = "Họ và tên không được vượt quá 120 ký tự.")
        String fullName,
        @NotBlank(message = "Mật khẩu không được để trống.")
        @Size(min = 8, max = 128, message = "Mật khẩu phải có từ 8 đến 128 ký tự.")
        String password,
        @NotBlank(message = "Vai trò không hợp lệ.")
        String role) {
}
