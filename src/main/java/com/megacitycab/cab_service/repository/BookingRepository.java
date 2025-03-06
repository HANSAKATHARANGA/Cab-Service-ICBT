package com.megacitycab.cab_service.repository;

import com.megacitycab.cab_service.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}