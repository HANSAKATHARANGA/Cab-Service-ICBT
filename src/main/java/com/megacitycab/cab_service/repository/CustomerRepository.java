package com.megacitycab.cab_service.repository;

import com.megacitycab.cab_service.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}