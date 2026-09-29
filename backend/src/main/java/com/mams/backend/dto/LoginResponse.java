package com.mams.backend.dto;

public class LoginResponse {
    private String token;
    private String username;
    private String role;
    private Long baseId;
    private String baseName;

    public LoginResponse(String token, String username, String role, Long baseId, String baseName) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.baseId = baseId;
        this.baseName = baseName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getBaseId() {
        return baseId;
    }

    public void setBaseId(Long baseId) {
        this.baseId = baseId;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }
}
