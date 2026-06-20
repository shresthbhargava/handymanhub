package com.handymanhub.security;

import com.handymanhub.model.User;

public interface JwtService {
    String generateToken(User user);
    String extractEmail(String token);
    String extractRole(String token);
    boolean isTokenValid(String token, String email);
}