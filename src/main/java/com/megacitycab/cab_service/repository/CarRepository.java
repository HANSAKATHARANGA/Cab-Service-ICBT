package com.megacitycab.cab_service.repository;

import com.megacitycab.cab_service.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}