package com.doan1.review.dto;

import java.util.UUID;

public record ReviewEligibility(boolean eligible, UUID targetId, String targetRole) {
}
