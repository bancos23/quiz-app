package com.example.quiz_app.service;

import com.example.quiz_app.model.User;
import com.example.quiz_app.model.Role;
import com.example.quiz_app.repository.UserRepository;
import com.example.quiz_app.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public User registerUser(User user) {
        // Normalize role assignment: allow client to specify role by id or name, otherwise default to 'user'
        Role resolved = null;
        if (user.getRole() != null) {
            Role provided = user.getRole();
            if (provided.getId() != null) {
                resolved = roleRepository.findById(provided.getId()).orElse(null);
            }
            if (resolved == null && provided.getName() != null) {
                resolved = roleRepository.findByNameIgnoreCase(provided.getName());
            }
        }
        if (resolved == null) {
            resolved = roleRepository.findByNameIgnoreCase("user");
            if (resolved == null) {
                resolved = roleRepository.save(new Role("user"));
            }
        }
        user.setRole(resolved);

        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll(); // @EntityGraph ensures roles are eagerly loaded
    }

    public User authenticate(String username, String rawPassword) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword() != null && user.getPassword().equals(rawPassword)) {
            return user;
        }
        return null;
    }
}