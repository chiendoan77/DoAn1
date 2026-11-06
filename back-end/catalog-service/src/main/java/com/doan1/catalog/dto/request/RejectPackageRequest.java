package com.doan1.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectPackageRequest(@NotBlank @Size(max = 1000) String reason) {
}
