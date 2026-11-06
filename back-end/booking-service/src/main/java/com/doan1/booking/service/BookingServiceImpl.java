package com.doan1.booking.service;

import com.doan1.booking.client.ProfileClient;
import com.doan1.booking.client.CatalogClient;
import com.doan1.booking.dto.request.CreateBookingRequest;
import com.doan1.booking.dto.response.BookingResponse;
import com.doan1.booking.dto.response.MarketplaceCategoryResponse;
import com.doan1.booking.dto.response.ProviderDetailsResponse;
import com.doan1.booking.dto.response.PackageDetailsResponse;
import com.doan1.booking.dto.response.ReviewEligibilityResponse;
import com.doan1.booking.entity.Booking;
import com.doan1.booking.entity.BookingOutboxEvent;
import com.doan1.booking.enums.BookingStatus;
import com.doan1.booking.mapper.BookingMapper;
import com.doan1.booking.repository.BookingRepository;
import com.doan1.booking.repository.BookingOutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookings;
    private final ProfileClient profiles;
    private final CatalogClient catalog;
    private final BookingOutboxEventRepository outbox;
    private final ObjectMapper objectMapper;

    public BookingServiceImpl(
            BookingRepository bookings,
            ProfileClient profiles,
            CatalogClient catalog,
            BookingOutboxEventRepository outbox,
            ObjectMapper objectMapper) {
        this.bookings = bookings;
        this.profiles = profiles;
        this.catalog = catalog;
        this.outbox = outbox;
        this.objectMapper = objectMapper;
    }

    @Override
    public BookingResponse create(UUID customerAccountId, String customerName, CreateBookingRequest input) {
        PackageDetailsResponse servicePackage = catalog.getApprovedPackage(input.packageId());
        ProviderDetailsResponse provider =
                profiles.getApprovedProvider(servicePackage.categoryId(), servicePackage.providerId());
        if (!provider.categoryIds().contains(servicePackage.categoryId()) || !"APPROVED".equals(provider.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nhà cung cấp không phục vụ danh mục của gói.");
        }
        String categoryName = provider.categories().stream()
                .filter(category -> category.id().equals(servicePackage.categoryId()))
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
                servicePackage.categoryId(),
                categoryName,
                servicePackage.id(),
                servicePackage.name(),
                servicePackage.price(),
                servicePackage.durationMinutes(),
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
        if (status == BookingStatus.COMPLETED) {
            writeCompletedEvent(booking);
        }
        return BookingMapper.toResponse(booking);
    }

    private void writeCompletedEvent(Booking booking) {
        UUID eventId = UUID.randomUUID();
        try {
            String payload = objectMapper.writeValueAsString(new BookingCompletedEvent(
                    eventId, "BookingCompleted", booking.getId(), booking.getCustomerAccountId(),
                    booking.getProviderAccountId(), booking.getCustomerName(), booking.getProviderName(),
                    booking.getPackageName()));
            outbox.save(new BookingOutboxEvent(eventId, booking.getId(), "BookingCompleted", payload));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize BookingCompleted event.", exception);
        }
    }

    private record BookingCompletedEvent(
            UUID eventId,
            String eventType,
            UUID bookingId,
            UUID customerAccountId,
            UUID providerAccountId,
            String customerName,
            String providerName,
            String packageName) {
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
