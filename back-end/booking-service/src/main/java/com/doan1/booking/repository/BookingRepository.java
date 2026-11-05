package com.doan1.booking.repository;

import com.doan1.booking.model.Booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByCustomerAccountIdOrderByCreatedAtDesc(UUID customerAccountId);

    List<Booking> findByProviderAccountIdOrderByCreatedAtDesc(UUID providerAccountId);
}
