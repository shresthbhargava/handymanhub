package com.handymanhub.service;

import com.handymanhub.exception.ResourceNotFoundException;
import com.handymanhub.model.RefreshToken;
import com.handymanhub.model.User;
import com.handymanhub.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// THIS IS THE MOST IMPORTANT CLASS IN THE REFRESH TOKEN SYSTEM.
//
// Let me walk you through the complete flow:
//
// CREATE:  User logs in → we call createToken(user)
//          → generate random UUID (e.g. "a1b2c3d4-...")
//          → SHA-256 hash it → store hash in DB
//          → return the raw UUID to the user
//          → user stores it in HttpOnly cookie or secure storage
//
// VERIFY:  User's access token expires → frontend calls /auth/refresh
//          → sends raw UUID → we hash it → find in DB
//          → check: not expired? not revoked?
//          → if valid: proceed to rotation
//          → if invalid: user must login again
//
// ROTATE:  delete old token from DB → create new token → return both
//          new access token + new refresh token to user
//          This is "token rotation" — even if hacker steals your
//          refresh token, they can only use it ONCE because the
//          moment they use it, it gets deleted.
//
// REVOKE:  User logs out → we find the token → set revoked=true
//          (we keep it in DB for audit, just mark it dead)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Service
public class RefreshTokenService {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    private final RefreshTokenRepository refreshTokenRepository;

    // How long a refresh token lives. Comes from application.yml.
    // Default: 7 days = 604800000 ms. Configurable per environment.
    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpirationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // ── CREATE ──────────────────────────────────────────────────
    // Called when user logs in or registers.
    // Returns the RAW token (UUID string) — never store this, send it to user.

    @Transactional
    public String createToken(User user) {
        // 1. Generate a cryptographically random UUID.
        //    UUID.randomUUID() uses java.security.SecureRandom internally —
        //    not predictable, not guessable. Good enough for tokens.
        String rawToken = UUID.randomUUID().toString();

        // 2. Hash it with SHA-256 before storing.
        //    This is the SAME idea as hashing passwords.
        //    If DB is leaked, attacker sees only hashes.
        String hash = hashToken(rawToken);

        // 3. Calculate expiry: now + 7 days
        // NOTE: LocalDateTime does NOT have plusMillis()!
        // It has plusNanos(), plusSeconds(), plusMinutes(), etc.
        // Convert ms to seconds: 604800000ms / 1000 = 604800 seconds
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(refreshExpirationMs / 1000);

        // 4. Build entity and save
        RefreshToken entity = RefreshToken.builder()
                .tokenHash(hash)
                .user(user)
                .expiresAt(expiresAt)
                .build();

        refreshTokenRepository.save(entity);

        log.info("Refresh token created for user id={} expires={}", user.getId(), expiresAt);
        return rawToken;  // Only the raw token leaves the server
    }

    // ── ROTATE ──────────────────────────────────────────────────
    // Called on POST /auth/refresh.
    // Takes the OLD raw token → verifies it → deletes it → creates new one.
    // Returns the NEW raw token.

    @Transactional
    public String rotateToken(String oldRawToken) {
        // 1. Verify the old token is valid (throws if not)
        RefreshToken oldToken = verifyToken(oldRawToken);

        // 2. Delete the old token from DB.
        //    After this call, the old token is GONE — can never be used again.
        //    This is the security property: each refresh token is single-use.
        refreshTokenRepository.deleteByTokenHash(oldToken.getTokenHash());

        log.info("Refresh token rotated (old deleted) for user id={}", oldToken.getUser().getId());

        // 3. Create a brand new token for the same user
        return createToken(oldToken.getUser());
    }

    // ── REVOKE ──────────────────────────────────────────────────
    // Called on POST /auth/logout.
    // Marks the token as revoked (kept in DB for audit trail).

    @Transactional
    public void revokeToken(String rawToken) {
        String hash = hashToken(rawToken);
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired refresh token"));

        token.setRevoked(true);
        refreshTokenRepository.save(token);

        log.info("Refresh token revoked for user id={}", token.getUser().getId());
    }

    // ── VERIFY (public) ─────────────────────────────────────────
    // Called by AuthService BEFORE rotation — it needs the User object
    // to generate a new access token. Rotation deletes the old token,
    // so we must extract the user FIRST.

    public User verifyAndGetUser(String rawToken) {
        RefreshToken token = verifyToken(rawToken);
        return token.getUser();
    }

    // ── VERIFY (internal) ───────────────────────────────────────
    // Called internally by verifyAndGetUser() and rotateToken().

    private RefreshToken verifyToken(String rawToken) {
        String hash = hashToken(rawToken);

        // 1. Find the token
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        // 2. Check if it was revoked (user logged out, or we detected fraud)
        if (token.isRevoked()) {
            throw new IllegalArgumentException("Refresh token has been revoked");
        }

        // 3. Check if it expired
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token has expired");
        }

        return token;
    }

    // ── HASHING ─────────────────────────────────────────────────
    // SHA-256 is NOT the same as BCrypt (which you use for passwords).
    // SHA-256 is a HASH function — one-way, fast, deterministic.
    // BCrypt is a PASSWORD HASH — intentionally slow (to resist brute force).
    //
    // For tokens, we use SHA-256 because:
    //   - We don't need slowness (tokens are 36-char UUIDs, not human passwords)
    //   - We need speed (verify happens on every /refresh request)
    //   - We just need "can't reverse it to get the original token"

    String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));

            // Convert bytes to hex string (64 characters)
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed to exist in every JRE since Java 1.4.
            // This should NEVER happen. But if it does, the app can't function.
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}