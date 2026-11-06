package com.doan1.profile.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerProfileRequest(
        @NotBlank @Size(max = 120) String displayName,
        @NotBlank @Pattern(regexp = "^\\+?[0-9][0-9\\s()-]{7,22}$") String phoneNumber,
        @NotBlank @Size(max = 500) String defaultAddress,
        @Size(max = 2048) @Pattern(regexp = "^$|https://.+") String avatarUrl) {
}
