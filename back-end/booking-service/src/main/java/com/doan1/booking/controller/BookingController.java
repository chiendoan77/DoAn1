package com.doan1.booking.controller;

import com.doan1.booking.dto.request.CreateBookingRequest;
import com.doan1.booking.dto.request.UpdateBookingStatusRequest;
import com.doan1.booking.dto.response.BookingResponse;
import com.doan1.booking.security.JwtPrincipalService;
import com.doan1.booking.service.BookingService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class BookingController {
    private final BookingService bookings;
    private final JwtPrincipalService principals;

    public BookingController(BookingService bookings, JwtPrincipalService principals) {
        this.bookings = bookings;
        this.principals = principals;
    }

    @PostMapping("/bookings")
    public BookingResponse create(HttpServletRequest request, @Valid @RequestBody CreateBookingRequest input) {
        JwtPrincipalService.Principal customer = principals.requireRole(request, "CUSTOMER");
        return bookings.create(customer.accountId(), customer.fullName(), input);
    }

    @GetMapping("/bookings/mine")
    public List<BookingResponse> history(HttpServletRequest request) {
        JwtPrincipalService.Principal principal = principals.authenticate(request);
        return bookings.history(principal.accountId(), principal.role());
    }

    @PatchMapping("/provider/bookings/{bookingId}/status")
    public BookingResponse updateStatus(
            HttpServletRequest request,
            @PathVariable UUID bookingId,
            @Valid @RequestBody UpdateBookingStatusRequest input) {
        JwtPrincipalService.Principal provider = principals.requireRole(request, "PROVIDER");
        return bookings.updateStatus(provider.accountId(), bookingId, input.status());
    }
}
