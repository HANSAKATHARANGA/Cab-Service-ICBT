package com.megacitycab.cab_service.repository;

import com.megacitycab.cab_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}