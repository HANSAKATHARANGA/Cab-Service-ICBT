package com.megacitycab.cab_service.repository;

import com.megacitycab.cab_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}