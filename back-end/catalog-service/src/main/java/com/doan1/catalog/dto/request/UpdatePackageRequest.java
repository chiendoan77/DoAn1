package com.doan1.catalog.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdatePackageRequest(
        @NotBlank @Size(max = 32) String categoryId,
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 2000) String description,
        @NotNull @DecimalMin("1") @Digits(integer = 10, fraction = 0) BigDecimal price,
        @NotNull @Min(1) @Max(1440) Integer durationMinutes,
        @NotBlank @Size(max = 32)
        @Pattern(regexp = "^(?=(?:\\D*\\d){8,15}\\D*$)[+0-9() .-]{8,32}$") String providerPhone,
        @NotNull @PositiveOrZero Long expectedVersion) {
}
