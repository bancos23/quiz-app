package com.example.quiz_app.controller.dto;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String roleName;

    public UserResponse() {}

    public UserResponse(Long id, String username, String email, String roleName) {
        this.id = id; this.username = username; this.email = email; this.roleName = roleName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
}

