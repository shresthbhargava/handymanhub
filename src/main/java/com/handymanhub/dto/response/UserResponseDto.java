package com.handymanhub.dto.response;

import java.time.LocalDateTime;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// Admin sees user list but NEVER sees passwords.
// This is why we have separate request/response DTOs —
// the User entity has a password field, but it must never
// reach the HTTP response. The DTO acts as a filter.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

public class UserResponseDto {

    private final Long id;
    private final String name;
    private final String email;
    private final String role;
    private final LocalDateTime createdAt;

    private UserResponseDto(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.role = builder.role;
        this.createdAt = builder.createdAt;
    }

    public Long getId()             { return id; }
    public String getName()         { return name; }
    public String getEmail()        { return email; }
    public String getRole()         { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name, email, role;
        private LocalDateTime createdAt;

        public Builder id(Long v)                    { this.id = v; return this; }
        public Builder name(String v)               { this.name = v; return this; }
        public Builder email(String v)              { this.email = v; return this; }
        public Builder role(String v)               { this.role = v; return this; }
        public Builder createdAt(LocalDateTime v)   { this.createdAt = v; return this; }

        public UserResponseDto build() { return new UserResponseDto(this); }
    }
}