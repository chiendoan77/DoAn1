package com.doan1.profile.dto.response;

import java.util.UUID;

public record ProviderEligibilityResponse(boolean eligible, UUID providerId) {
}
