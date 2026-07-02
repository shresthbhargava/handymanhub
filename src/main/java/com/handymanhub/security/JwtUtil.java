package com.handymanhub.security;

import com.handymanhub.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HexFormat;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// WHAT CHANGED FROM YOUR ORIGINAL JwtUtil:
//
// 1. generateToken() now includes user.getId() as a claim.
//    Before: {sub: "email", role: "CUSTOMER", name: "Shresth"}
//    After:  {sub: "email", role: "CUSTOMER", name: "Shresth", userId: 5}
//
//    WHY: Your frontend was doing an extra API call to find the customer ID
//    by email just to create a booking. Now it just reads userId from the token.
//    That's one fewer API call on every booking creation.
//
// 2. Added extractUserId() — reads the userId claim back out.
//
// EVERYTHING ELSE IS UNCHANGED. Your existing tokens still work until they expire.
// New tokens generated after this change will include userId.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Component
public class JwtUtil implements JwtService  {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {
        byte[] keyBytes = HexFormat.of().parseHex(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expiration;
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("name", user.getName())
                .claim("userId", user.getId())     // ← NEW: frontend gets ID from token
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    // NEW — extract user's database ID from the JWT claims
    public Long extractUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }

    public boolean isTokenValid(String token, String email) {
        try {
            String extracted = extractEmail(token);
            return extracted.equals(email) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}