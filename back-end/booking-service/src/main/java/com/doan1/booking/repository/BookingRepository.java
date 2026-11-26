package com.doan1.booking.repository;

import com.doan1.booking.entity.Booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByCustomerAccountIdOrderByCreatedAtDesc(UUID customerAccountId);

    List<Booking> findByProviderAccountIdOrderByCreatedAtDesc(UUID providerAccountId);

    Page<Booking> findByCustomerAccountIdOrderByCreatedAtDesc(UUID customerAccountId, Pageable pageable);
    Page<Booking> findByProviderAccountIdOrderByCreatedAtDesc(UUID providerAccountId, Pageable pageable);
    boolean existsByCustomerAccountIdAndPackageIdAndScheduledAtAndStatus(
            UUID customerAccountId, UUID packageId, java.time.OffsetDateTime scheduledAt,
            com.doan1.booking.enums.BookingStatus status);
}
