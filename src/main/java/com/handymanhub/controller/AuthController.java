package com.handymanhub.controller;

import com.handymanhub.dto.request.LoginRequestDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Register and login to get a JWT token")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new CUSTOMER account and returns a JWT token immediately"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered, token returned"),
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
            description = "Returns a JWT token valid for 24 hours. Include in all subsequent requests as: Authorization: Bearer <token>"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, token returned"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}