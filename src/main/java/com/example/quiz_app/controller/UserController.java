package com.example.quiz_app.controller;

import com.example.quiz_app.controller.dto.RegisterRequest;
import com.example.quiz_app.controller.dto.UserResponse;
import com.example.quiz_app.model.Role;
import com.example.quiz_app.model.User;
import com.example.quiz_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest req) {
        String username = req.getUsername() != null ? req.getUsername().trim() : null;
        String email = req.getEmail() != null ? req.getEmail().trim() : null;

        if (username != null && userService.findByUsername(username) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        if (email != null && userService.findByEmail(email) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(req.getPassword());

        // Attach role if provided
        if (req.getRoleId() != null || (req.getRoleName() != null && !req.getRoleName().isBlank())) {
            Role r = new Role();
            r.setId(req.getRoleId());
            r.setName(req.getRoleName());
            user.setRole(r);
        }

        User newUser = userService.registerUser(user);
        String roleName = newUser.getRole() != null ? newUser.getRole().getName() : null;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserResponse(newUser.getId(), newUser.getUsername(), newUser.getEmail(), roleName));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> loginUser(@RequestBody User loginRequest) {
        User user = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        if (user != null) {
            String roleName = user.getRole() != null ? user.getRole().getName() : null;
            return ResponseEntity.ok(new UserResponse(user.getId(), user.getUsername(), user.getEmail(), roleName));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/all")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(u -> new UserResponse(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getRole() != null ? u.getRole().getName() : null
                ))
                .collect(Collectors.toList());
    }
}