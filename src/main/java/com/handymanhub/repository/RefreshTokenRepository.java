package com.handymanhub.repository;

import com.handymanhub.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// Only 4 queries we need. Let's understand WHY each exists:
//
// 1. findByTokenHash — the core lookup. User sends raw token,
//    we hash it, then look it up here. This is called on every /refresh.
//
// 2. deleteByTokenHash — token rotation. After successfully using
//    a refresh token, we DELETE the old one and create a new one.
//    This ensures each refresh token is single-use.
//
// 3. revokeAllByUserId — "logout from all devices". If a user
//    reports their account compromised, we revoke every token.
//    @Modifying + @Query because Spring Data can't derive
//    "update ... set revoked = true where user_id = ?" automatically.
//
// 4. deleteAllByExpiresAtBefore — cleanup job. Run this periodically
//    to remove expired tokens and keep the table small.
//    Without cleanup, the table grows forever.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Called on every /refresh request
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // Called after successful refresh (token rotation)
    void deleteByTokenHash(String tokenHash);

    // Called on "logout all devices" (future feature)
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user.id = :userId")
    void revokeAllByUserId(Long userId);

    // Called by a scheduled cleanup job (keeps table small)
    void deleteAllByExpiresAtBefore(LocalDateTime now);
}