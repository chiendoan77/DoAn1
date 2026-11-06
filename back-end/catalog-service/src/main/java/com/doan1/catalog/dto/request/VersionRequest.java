package com.doan1.catalog.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record VersionRequest(@NotNull @PositiveOrZero Long expectedVersion) {
}
