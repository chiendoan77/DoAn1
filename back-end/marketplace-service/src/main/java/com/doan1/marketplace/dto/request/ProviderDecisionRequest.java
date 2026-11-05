package com.doan1.marketplace.dto.request;

import jakarta.validation.constraints.Size;

public record ProviderDecisionRequest(boolean approved, @Size(max = 500) String reason) {
}
