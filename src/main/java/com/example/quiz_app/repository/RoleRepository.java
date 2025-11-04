package com.example.quiz_app.repository;

import com.example.quiz_app.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String roleName);
    Role findByNameIgnoreCase(String roleName);
}