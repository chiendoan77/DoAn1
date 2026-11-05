package com.doan1.booking.dto.response;

import com.doan1.booking.entity.Booking;

import java.util.UUID;

public record ReviewEligibilityResponse(boolean eligible, UUID targetId, String targetRole) {
}
