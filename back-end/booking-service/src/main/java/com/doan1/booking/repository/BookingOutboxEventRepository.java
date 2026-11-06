package com.doan1.booking.repository;

import com.doan1.booking.entity.BookingOutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingOutboxEventRepository extends JpaRepository<BookingOutboxEvent, UUID> {
    List<BookingOutboxEvent> findTop50ByStatusOrderByCreatedAtAsc(String status);
}
