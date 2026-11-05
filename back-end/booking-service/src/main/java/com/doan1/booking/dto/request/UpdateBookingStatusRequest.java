package com.doan1.booking.dto.request;

import com.doan1.booking.entity.Booking;
import com.doan1.booking.enums.BookingStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateBookingStatusRequest(@NotNull BookingStatus status) {
}
