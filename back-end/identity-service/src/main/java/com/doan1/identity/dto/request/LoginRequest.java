package com.doan1.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank String identifier,
        @NotBlank @Size(max = 72) String password) {
}
