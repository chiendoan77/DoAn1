package com.doan1.profile.dto.response;

import java.time.Instant;

public record CustomerProfileResponse(String displayName, String phoneNumber, String defaultAddress, Instant updatedAt) {
}
