package com.doan1.booking.service;

import com.doan1.booking.dto.request.CreateBookingRequest;
import com.doan1.booking.dto.response.BookingResponse;
import com.doan1.booking.dto.response.ReviewEligibilityResponse;
import com.doan1.booking.enums.BookingStatus;

import java.util.List;
import java.util.UUID;

public interface BookingService {
    BookingResponse create(UUID customerAccountId, String customerName, CreateBookingRequest input);

    List<BookingResponse> history(UUID accountId, String role);

    BookingResponse updateStatus(UUID providerAccountId, UUID bookingId, BookingStatus status);

    ReviewEligibilityResponse reviewEligibility(UUID bookingId, UUID reviewerAccountId, String reviewerRole);
}
