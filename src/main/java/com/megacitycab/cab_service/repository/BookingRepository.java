package com.megacitycab.cab_service.repository;

import com.megacitycab.cab_service.model.Booking;
import com.megacitycab.cab_service.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomer(Customer customer);
}