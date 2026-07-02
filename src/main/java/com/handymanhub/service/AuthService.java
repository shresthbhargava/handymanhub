package com.handymanhub.service;

import com.handymanhub.dto.request.LoginRequestDto;
import com.handymanhub.dto.request.RefreshTokenRequestDto;
import com.handymanhub.dto.request.RegisterRequestDto;
import com.handymanhub.dto.response.AuthResponseDto;
import com.handymanhub.model.User;
import com.handymanhub.repository.UserRepository;
import com.handymanhub.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// WHAT CHANGED vs your original AuthService:
//
// 1. Constructor takes RefreshTokenService as a new dependency.
//
// 2. register() and login() now also call refreshTokenService.createToken(user)
//    and include both tokens + userId in the response.
//
// 3. refreshToken() is NEW — the /auth/refresh flow:
//    Step 1: verifyAndGetUser(oldToken)  → validates token, returns User
//    Step 2: jwtUtil.generateToken(user) → new access token
//    Step 3: rotateToken(oldToken)       → deletes old, creates new refresh token
//    Step 4: return both new tokens
//
//    ORDER MATTERS: We get the user BEFORE rotating because
//    rotation deletes the old token from the database.
//
// 4. logout() is NEW — revokes the refresh token.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtUtil,
                       AuthenticationManager authenticationManager,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthResponseDto register(RegisterRequestDto dto) {
        log.info("Registering new user email={}", dto.getEmail());

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + dto.getEmail());
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(User.Role.CUSTOMER)
                .build();

        User saved = userRepository.save(user);
        String accessToken = jwtUtil.generateToken(saved);
        String refreshToken = refreshTokenService.createToken(saved);

        log.info("User registered id={} email={}", saved.getId(), saved.getEmail());

        return AuthResponseDto.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .userId(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .role(saved.getRole().name())
                .build();
    }

    public AuthResponseDto login(LoginRequestDto dto) {
        log.info("Login attempt email={}", dto.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = refreshTokenService.createToken(user);

        log.info("User logged in email={}", dto.getEmail());

        return AuthResponseDto.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    // ── NEW ─────────────────────────────────────────────────────
    @Transactional
    public AuthResponseDto refreshToken(RefreshTokenRequestDto dto) {
        String oldRawToken = dto.getRefreshToken();
        log.info("Token refresh request received");

        // Step 1: Get the user from the old token BEFORE rotating.
        // If we rotated first, the old token would be deleted and we'd
        // lose the ability to look up the user.
        User user = refreshTokenService.verifyAndGetUser(oldRawToken);

        // Step 2: Generate a new access token for this user
        String newAccessToken = jwtUtil.generateToken(user);

        // Step 3: Rotate the refresh token (old deleted, new created)
        String newRefreshToken = refreshTokenService.rotateToken(oldRawToken);

        log.info("Tokens refreshed for user id={}", user.getId());

        return AuthResponseDto.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public void logout(RefreshTokenRequestDto dto) {
        log.info("Logout request received");
        refreshTokenService.revokeToken(dto.getRefreshToken());
        log.info("User logged out successfully");
    }
}