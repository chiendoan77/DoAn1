package com.doan1.booking.dto;

import com.doan1.booking.model.Booking;
import com.doan1.booking.model.BookingStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateBookingStatusRequest(@NotNull BookingStatus status) {
}
