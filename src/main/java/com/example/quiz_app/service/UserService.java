package com.example.quiz_app.service;

import com.example.quiz_app.model.User;
import com.example.quiz_app.model.Role;
import com.example.quiz_app.repository.UserRepository;
import com.example.quiz_app.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String rawPassword, String email) {
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already exists");
        }

        Role userRole = roleRepository.findByNameIgnoreCase("user");
        if (userRole == null) {
            userRole = roleRepository.save(new Role("user"));
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEmail(email);
        user.setRole(userRole);

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User authenticate(String username, String rawPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null)
            return null;

        if (!passwordEncoder.matches(rawPassword, user.getPassword()))
            return null;

        return user;
    }
}