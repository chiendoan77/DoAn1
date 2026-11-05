package com.doan1.marketplace.dto.response;

import java.util.UUID;

public record ReviewEligibility(boolean eligible, UUID targetId, String targetRole) {
}
