package com.handymanhub.controller;

import com.handymanhub.dto.request.LoginRequestDto;
import com.handymanhub.dto.request.RefreshTokenRequestDto;
import com.handymanhub.dto.request.RegisterRequestDto;
import com.handymanhub.dto.response.AuthResponseDto;
import com.handymanhub.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 2 new endpoints added:
//
// POST /api/v1/auth/refresh  — public (no auth needed)
//   Frontend calls this when access token expires.
//   Body: { "refreshToken": "uuid-string" }
//   Returns: same AuthResponseDto with new tokens
//
// POST /api/v1/auth/logout   — PROTECTED (requires valid access token)
//   Frontend calls this on logout button click.
//   Body: { "refreshToken": "uuid-string" }
//   Returns: 204 No Content
//
// WHY IS /LOGOUT PROTECTED BUT /REFRESH ISN'T?
//   /refresh can't require auth — the access token is EXPIRED,
//   that's why the user is calling /refresh in the first place!
//   The refresh token itself serves as proof of identity.
//
//   /logout IS protected because we want to verify WHO is logging out.
//   A random person shouldn't be able to revoke someone else's token.
//   The access token tells us who the authenticated user is.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Tag(name = "Authentication", description = "Register, login, refresh tokens, and logout")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new CUSTOMER account. Returns both access token (15 min) and refresh token (7 days)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered, both tokens returned"),
            @ApiResponse(responseCode = "400", description = "Email already registered or invalid input")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(
            @Valid @RequestBody RegisterRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(dto));
    }

    @Operation(
            summary = "Login with email and password",
            description = "Returns access token (15 min) + refresh token (7 days). Use access token in Authorization header. When access token expires, call /refresh."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, both tokens returned"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    // ── NEW ENDPOINT ────────────────────────────────────────────
    @Operation(
            summary = "Refresh access token",
            description = "Sends your refresh token, gets back a new access token + new refresh token. Old refresh token is immediately invalidated (rotation)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "New tokens issued"),
            @ApiResponse(responseCode = "400", description = "Invalid, expired, or revoked refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(
            @Valid @RequestBody RefreshTokenRequestDto dto) {
        return ResponseEntity.ok(authService.refreshToken(dto));
    }

    // ── NEW ENDPOINT ────────────────────────────────────────────
    @Operation(
            summary = "Logout (revoke refresh token)",
            description = "Requires a valid access token in Authorization header. Revokes the refresh token so it cannot be reused."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logged out successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenRequestDto dto) {
        authService.logout(dto);
        return ResponseEntity.noContent().build();
    }
}