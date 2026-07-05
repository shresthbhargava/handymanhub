package com.handymanhub.service;

import com.handymanhub.dto.request.LoginRequestDto;
import com.handymanhub.dto.request.RefreshTokenRequestDto;
import com.handymanhub.dto.request.RegisterRequestDto;
import com.handymanhub.dto.response.AuthResponseDto;
import com.handymanhub.model.Customer;
import com.handymanhub.model.User;
import com.handymanhub.repository.CustomerRepository;
import com.handymanhub.repository.UserRepository;
import com.handymanhub.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository,
                       CustomerRepository customerRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtUtil,
                       AuthenticationManager authenticationManager,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    private Long getOrCreateCustomer(User user) {
        return customerRepository.findByEmail(user.getEmail())
                .map(Customer::getId)
                .orElseGet(() -> {
                    Customer c = new Customer();
                    c.setName(user.getName());
                    c.setEmail(user.getEmail());
                    return customerRepository.save(c).getId();
                });
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
        Long customerId = getOrCreateCustomer(saved);
        String accessToken = jwtUtil.generateToken(saved);
        String refreshToken = refreshTokenService.createToken(saved);

        log.info("User registered id={} email={} customerId={}", saved.getId(), saved.getEmail(), customerId);

        return AuthResponseDto.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .userId(saved.getId())
                .customerId(customerId)
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

        Long customerId = getOrCreateCustomer(user);
        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = refreshTokenService.createToken(user);

        log.info("User logged in email={} customerId={}", dto.getEmail(), customerId);

        return AuthResponseDto.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .customerId(customerId)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public AuthResponseDto refreshToken(RefreshTokenRequestDto dto) {
        String oldRawToken = dto.getRefreshToken();
        log.info("Token refresh request received");

        User user = refreshTokenService.verifyAndGetUser(oldRawToken);
        Long customerId = getOrCreateCustomer(user);
        String newAccessToken = jwtUtil.generateToken(user);
        String newRefreshToken = refreshTokenService.rotateToken(oldRawToken);

        log.info("Tokens refreshed for user id={}", user.getId());

        return AuthResponseDto.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .userId(user.getId())
                .customerId(customerId)
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
