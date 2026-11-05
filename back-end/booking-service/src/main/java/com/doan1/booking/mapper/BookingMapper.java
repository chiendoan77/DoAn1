package com.doan1.booking.mapper;

import com.doan1.booking.dto.response.BookingResponse;
import com.doan1.booking.entity.Booking;

import java.util.List;

public final class BookingMapper {
    private BookingMapper() {
    }

    public static BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getCategoryId(),
                booking.getCategoryName(),
                booking.getProviderProfileId(),
                booking.getProviderName(),
                booking.getCustomerName(),
                booking.getScheduledAt(),
                booking.getAddress(),
                booking.getDescription(),
                booking.getStatus(),
                booking.getCreatedAt());
    }

    public static List<BookingResponse> toResponses(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toResponse).toList();
    }
}
