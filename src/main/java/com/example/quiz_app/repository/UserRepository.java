package com.example.quiz_app.repository;

import com.example.quiz_app.model.Role;
import com.example.quiz_app.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);

    @EntityGraph(attributePaths = {"role"})
    List<User> findAll();
}