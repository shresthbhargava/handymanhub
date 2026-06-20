package com.handymanhub.service;

import com.handymanhub.dto.request.LoginRequestDto;
import com.handymanhub.dto.request.RegisterRequestDto;
import com.handymanhub.dto.response.AuthResponseDto;
import com.handymanhub.model.User;
import com.handymanhub.repository.UserRepository;
import com.handymanhub.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.handymanhub.security.JwtService;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtUtil;
    private final AuthenticationManager authenticationManager;


    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
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
        String token = jwtUtil.generateToken(saved);

        log.info("User registered id={} email={}", saved.getId(), saved.getEmail());

        return AuthResponseDto.builder()
                .token(token)
                .name(saved.getName())
                .email(saved.getEmail())
                .role(saved.getRole().name())
                .build();
    }

    public AuthResponseDto login(LoginRequestDto dto) {
        log.info("Login attempt email={}", dto.getEmail());

        // This throws AuthenticationException if credentials are wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtUtil.generateToken(user);
        log.info("User logged in email={}", dto.getEmail());

        return AuthResponseDto.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}