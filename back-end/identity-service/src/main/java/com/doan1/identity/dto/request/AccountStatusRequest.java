package com.doan1.identity.dto.request;

import com.doan1.identity.enums.AccountStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AccountStatusRequest(@NotNull AccountStatus status, @NotBlank @Size(max = 500) String reason) {
}
