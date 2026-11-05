package com.doan1.identity.dto.response;

import com.doan1.identity.enums.Role;

import java.util.UUID;

public record AccountResponse(UUID id, String email, String phoneNumber, String username, String fullName, Role role) {
}
