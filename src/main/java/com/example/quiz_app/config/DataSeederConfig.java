package com.example.quiz_app.config;

import com.example.quiz_app.model.Role;
import com.example.quiz_app.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeederConfig {
    @Bean
    CommandLineRunner seedDefaultRoles(RoleRepository roles) {
        return args -> {
            ensureRole(roles, "user");
            ensureRole(roles, "admin");
        };
    }

    private void ensureRole(RoleRepository roles, String name) {
        Role existing = roles.findByNameIgnoreCase(name);
        if (existing == null) {
            Role created = roles.save(new Role(name));
        }
    }
}

