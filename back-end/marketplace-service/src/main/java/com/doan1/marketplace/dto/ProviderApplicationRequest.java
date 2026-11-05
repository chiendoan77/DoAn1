package com.doan1.marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProviderApplicationRequest(
        @NotEmpty @Size(max = 2) List<@NotBlank @Pattern(regexp = "cleaning|repair") String> categoryIds,
        @NotBlank @Size(max = 500) String address,
        @NotBlank @Pattern(regexp = "https://.+") @Size(max = 2048) String imageUrl,
        @NotBlank @Size(max = 1000) String description) {
}
