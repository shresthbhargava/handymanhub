package com.handymanhub.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    public enum Role { CUSTOMER, ADMIN }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.CUSTOMER;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public User() {}

    // ── UserDetails interface methods ─────────────────────────────
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email; // we use email as username
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired()     { return true; }

    @Override
    public boolean isAccountNonLocked()      { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled()               { return true; }

    // ── Getters and Setters ───────────────────────────────────────
    public Long getId()                    { return id; }
    public void setId(Long id)             { this.id = id; }

    public String getName()                { return name; }
    public void setName(String name)       { this.name = name; }

    public String getEmail()               { return email; }
    public void setEmail(String email)     { this.email = email; }


    public void setPassword(String password){ this.password = password; }

    public Role getRole()                  { return role; }
    public void setRole(Role role)         { this.role = role; }

    public LocalDateTime getCreatedAt()    { return createdAt; }

    public static Builder builder()        { return new Builder(); }

    public static class Builder {
        private String name, email, password;
        private Role role = Role.CUSTOMER;

        public Builder name(String v)      { this.name = v; return this; }
        public Builder email(String v)     { this.email = v; return this; }
        public Builder password(String v)  { this.password = v; return this; }
        public Builder role(Role v)        { this.role = v; return this; }

        public User build() {
            User u = new User();
            u.name = this.name;
            u.email = this.email;
            u.password = this.password;
            u.role = this.role;
            return u;
        }
    }
}