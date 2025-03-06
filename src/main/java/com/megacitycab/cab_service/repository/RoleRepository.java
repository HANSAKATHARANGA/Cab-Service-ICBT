package com.megacitycab.cab_service.repository;

import com.megacitycab.cab_service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}