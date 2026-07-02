package com.handymanhub.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// WHY THIS ENTITY EXISTS:
//   This maps to the refresh_tokens table we just created.
//   Every row in that table = one refresh token = one logged-in device.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // SHA-256 hash of the actual UUID token the user holds.
    // We NEVER store the raw token in the database.
    // If the DB is compromised, attacker gets useless hashes.
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    // Many refresh tokens can belong to one user (multiple devices).
    // EAGER because we always need the user when we find a refresh token
    // — to generate a new access token for them.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean revoked = false;

    // ── Constructors ────────────────────────────────────────────
    // JPA requires a no-arg constructor (creates objects from DB rows)
    public RefreshToken() {}

    // ── Getters and Setters ─────────────────────────────────────
    public Long getId()           { return id; }
    public void setId(Long id)    { this.id = id; }

    public String getTokenHash()              { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public User getUser()              { return user; }
    public void setUser(User user)     { this.user = user; }

    public LocalDateTime getExpiresAt()              { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public boolean isRevoked()              { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }

    // ── Builder (matches your project's style) ──────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String tokenHash;
        private User user;
        private LocalDateTime expiresAt;
        private boolean revoked = false;

        public Builder tokenHash(String v)       { this.tokenHash = v; return this; }
        public Builder user(User v)              { this.user = v; return this; }
        public Builder expiresAt(LocalDateTime v){ this.expiresAt = v; return this; }
        public Builder revoked(boolean v)        { this.revoked = v; return this; }

        public RefreshToken build() {
            RefreshToken rt = new RefreshToken();
            rt.tokenHash = this.tokenHash;
            rt.user = this.user;
            rt.expiresAt = this.expiresAt;
            rt.revoked = this.revoked;
            return rt;
        }
    }
}