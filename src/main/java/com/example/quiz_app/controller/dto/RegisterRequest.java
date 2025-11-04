package com.example.quiz_app.controller.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String email;

    private Long roleId; // optional
    @JsonAlias({"role", "roleName"})
    private String roleName; // optional, accepts JSON key "role" too

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
}
