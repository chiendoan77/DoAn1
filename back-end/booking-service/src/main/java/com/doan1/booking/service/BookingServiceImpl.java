package com.doan1.booking.service;

import com.doan1.booking.client.MarketplaceClient;
import com.doan1.booking.dto.request.CreateBookingRequest;
import com.doan1.booking.dto.response.BookingResponse;
import com.doan1.booking.dto.response.MarketplaceCategoryResponse;
import com.doan1.booking.dto.response.ProviderDetailsResponse;
import com.doan1.booking.dto.response.ReviewEligibilityResponse;
import com.doan1.booking.entity.Booking;
import com.doan1.booking.enums.BookingStatus;
import com.doan1.booking.mapper.BookingMapper;
import com.doan1.booking.repository.BookingRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookings;
    private final MarketplaceClient marketplace;

    public BookingServiceImpl(BookingRepository bookings, MarketplaceClient marketplace) {
        this.bookings = bookings;
        this.marketplace = marketplace;
    }

    @Override
    public BookingResponse create(UUID customerAccountId, String customerName, CreateBookingRequest input) {
        ProviderDetailsResponse provider = marketplace.getApprovedProvider(input.categoryId(), input.providerId());
        if (!provider.categoryIds().contains(input.categoryId()) || !"APPROVED".equals(provider.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nhà cung cấp không phục vụ danh mục đã chọn.");
        }
        String categoryName = provider.categories().stream()
                .filter(category -> category.id().equals(input.categoryId()))
                .map(MarketplaceCategoryResponse::name)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Danh mục không thuộc hồ sơ nhà cung cấp."));
        Booking booking = bookings.save(new Booking(
                customerAccountId,
                provider.id(),
                provider.identityAccountId(),
                customerName,
                provider.fullName(),
                input.categoryId(),
                categoryName,
                input.scheduledAt(),
                input.address().trim(),
                input.description().trim()));
        return BookingMapper.toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> history(UUID accountId, String role) {
        if ("CUSTOMER".equals(role)) {
            return BookingMapper.toResponses(bookings.findByCustomerAccountIdOrderByCreatedAtDesc(accountId));
        }
        if ("PROVIDER".equals(role)) {
            return BookingMapper.toResponses(bookings.findByProviderAccountIdOrderByCreatedAtDesc(accountId));
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xem lịch sử này.");
    }

    @Override
    @Transactional
    public BookingResponse updateStatus(UUID providerAccountId, UUID bookingId, BookingStatus status) {
        Booking booking = bookings.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu đặt lịch."));
        if (!booking.getProviderAccountId().equals(providerAccountId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không được cập nhật lịch hẹn này.");
        }
        boolean validTransition = switch (booking.getStatus()) {
            case REQUESTED -> status == BookingStatus.ACCEPTED || status == BookingStatus.REJECTED;
            case ACCEPTED -> status == BookingStatus.COMPLETED;
            case REJECTED, COMPLETED -> false;
        };
        if (!validTransition) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Không thể chuyển trạng thái lịch hẹn này.");
        }
        booking.setStatus(status);
        return BookingMapper.toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewEligibilityResponse reviewEligibility(UUID bookingId, UUID reviewerAccountId, String reviewerRole) {
        Booking booking = bookings.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found."));
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            return new ReviewEligibilityResponse(false, null, null);
        }
        if ("CUSTOMER".equals(reviewerRole) && booking.getCustomerAccountId().equals(reviewerAccountId)) {
            return new ReviewEligibilityResponse(true, booking.getProviderProfileId(), "PROVIDER");
        }
        if ("PROVIDER".equals(reviewerRole) && booking.getProviderAccountId().equals(reviewerAccountId)) {
            return new ReviewEligibilityResponse(true, booking.getCustomerAccountId(), "CUSTOMER");
        }
        return new ReviewEligibilityResponse(false, null, null);
    }
}
