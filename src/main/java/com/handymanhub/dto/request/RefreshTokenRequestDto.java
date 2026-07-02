package com.handymanhub.dto.request;

import jakarta.validation.constraints.NotBlank;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// Simple DTO for the refresh endpoint.
// Frontend sends: { "refreshToken": "uuid-string" }
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

public class RefreshTokenRequestDto {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

    public String getRefreshToken()              { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}