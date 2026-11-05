package com.doan1.booking.controller;

import com.doan1.booking.client.MarketplaceClient;
import com.doan1.booking.dto.BookingResponse;
import com.doan1.booking.dto.CreateBookingRequest;
import com.doan1.booking.dto.ProviderDetailsResponse;
import com.doan1.booking.dto.UpdateBookingStatusRequest;
import com.doan1.booking.model.Booking;
import com.doan1.booking.model.BookingStatus;
import com.doan1.booking.repository.BookingRepository;
import com.doan1.booking.security.JwtPrincipalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class BookingController {
    private final BookingRepository bookings;
    private final JwtPrincipalService principals;
    private final MarketplaceClient marketplace;

    public BookingController(BookingRepository bookings, JwtPrincipalService principals, MarketplaceClient marketplace) {
        this.bookings = bookings;
        this.principals = principals;
        this.marketplace = marketplace;
    }

    @PostMapping("/bookings")
    public BookingResponse create(HttpServletRequest request, @Valid @RequestBody CreateBookingRequest input) {
        JwtPrincipalService.Principal customer = principals.requireRole(request, "CUSTOMER");
        ProviderDetailsResponse provider = marketplace.getApprovedProvider(input.categoryId(), input.providerId());
        if (!provider.categoryIds().contains(input.categoryId()) || !"APPROVED".equals(provider.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nhà cung cấp không phục vụ danh mục đã chọn.");
        }
        String categoryName = provider.categories().stream()
                .filter(category -> category.id().equals(input.categoryId()))
                .map(category -> category.name())
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh mục không thuộc hồ sơ nhà cung cấp."));
        Booking booking = bookings.save(new Booking(
                customer.accountId(),
                provider.id(),
                provider.identityAccountId(),
                customer.fullName(),
                provider.fullName(),
                input.categoryId(),
                categoryName,
                input.scheduledAt(),
                input.address().trim(),
                input.description().trim()));
        return BookingResponse.from(booking);
    }

    @GetMapping("/bookings/mine")
    public List<BookingResponse> history(HttpServletRequest request) {
        JwtPrincipalService.Principal principal = principals.authenticate(request);
        if ("CUSTOMER".equals(principal.role())) {
            return bookings.findByCustomerAccountIdOrderByCreatedAtDesc(principal.accountId())
                    .stream().map(BookingResponse::from).toList();
        }
        if (!"PROVIDER".equals(principal.role())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xem lịch sử này.");
        }
        return bookings.findByProviderAccountIdOrderByCreatedAtDesc(principal.accountId())
                .stream().map(BookingResponse::from).toList();
    }

    @PatchMapping("/provider/bookings/{bookingId}/status")
    @Transactional
    public BookingResponse updateStatus(
            HttpServletRequest request,
            @PathVariable UUID bookingId,
            @Valid @RequestBody UpdateBookingStatusRequest input) {
        JwtPrincipalService.Principal provider = principals.requireRole(request, "PROVIDER");
        Booking booking = bookings.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu đặt lịch."));
        if (!booking.getProviderAccountId().equals(provider.accountId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không được cập nhật lịch hẹn này.");
        }
        boolean validTransition = switch (booking.getStatus()) {
            case REQUESTED -> input.status() == BookingStatus.ACCEPTED || input.status() == BookingStatus.REJECTED;
            case ACCEPTED -> input.status() == BookingStatus.COMPLETED;
            case REJECTED, COMPLETED -> false;
        };
        if (!validTransition) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Không thể chuyển trạng thái lịch hẹn này.");
        }
        booking.setStatus(input.status());
        return BookingResponse.from(booking);
    }

}
