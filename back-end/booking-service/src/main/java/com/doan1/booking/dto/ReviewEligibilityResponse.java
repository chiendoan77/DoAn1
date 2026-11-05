package com.doan1.booking.dto;

import com.doan1.booking.model.Booking;

import java.util.UUID;

public record ReviewEligibilityResponse(boolean eligible, UUID targetId, String targetRole) {
}
