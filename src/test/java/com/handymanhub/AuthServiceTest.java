package com.handymanhub;

import com.handymanhub.dto.request.LoginRequestDto;
import com.handymanhub.dto.request.RegisterRequestDto;
import com.handymanhub.dto.response.AuthResponseDto;
import com.handymanhub.model.User;
import com.handymanhub.repository.UserRepository;
import com.handymanhub.security.JwtUtil;
import com.handymanhub.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import com.handymanhub.security.JwtService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtUtil;
    @Mock AuthenticationManager authenticationManager;

    @InjectMocks
    AuthService authService;

    private RegisterRequestDto registerDto;
    private LoginRequestDto loginDto;
    private User user;

    @BeforeEach
    void setUp() {
        registerDto = new RegisterRequestDto();
        registerDto.setName("Shresth Bhargava");
        registerDto.setEmail("shresth@gmail.com");
        registerDto.setPassword("shresth123");

        loginDto = new LoginRequestDto();
        loginDto.setEmail("shresth@gmail.com");
        loginDto.setPassword("shresth123");

        user = User.builder()
                .name("Shresth Bhargava")
                .email("shresth@gmail.com")
                .password("$2a$10$hashedpassword")
                .role(User.Role.CUSTOMER)
                .build();
    }

    // ── Register tests ────────────────────────────────────────────

    @Test
    @DisplayName("Should register a new user successfully")
    void register_success() {
        when(userRepository.existsByEmail("shresth@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("shresth123")).thenReturn("$2a$10$hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("mock.jwt.token");

        AuthResponseDto result = authService.register(registerDto);

        assertNotNull(result);
        assertEquals("mock.jwt.token", result.getToken());
        assertEquals("shresth@gmail.com", result.getEmail());
        assertEquals("CUSTOMER", result.getRole());

        // verify password was hashed before saving
        verify(passwordEncoder).encode("shresth123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already registered")
    void register_duplicateEmail_throwsException() {
        when(userRepository.existsByEmail("shresth@gmail.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(registerDto)
        );

        assertEquals("Email already registered: shresth@gmail.com", ex.getMessage());

        // critical — verify save was never called
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("Should hash password before saving — never store plain text")
    void register_passwordIsHashed() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("shresth123")).thenReturn("$2a$10$hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("token");

        authService.register(registerDto);

        // capture what was actually saved
        verify(userRepository).save(argThat(savedUser ->
                savedUser.getPassword().equals("$2a$10$hashedpassword") &&
                        !savedUser.getPassword().equals("shresth123")
        ));
    }

    @Test
    @DisplayName("Should return token immediately after registration")
    void register_returnsTokenImmediately() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("immediate.token");

        AuthResponseDto result = authService.register(registerDto);

        assertNotNull(result.getToken());
        assertEquals("immediate.token", result.getToken());
    }

    // ── Login tests ───────────────────────────────────────────────

    @Test
    @DisplayName("Should login successfully with correct credentials")
    void login_success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null); // authenticate returns null on success
        when(userRepository.findByEmail("shresth@gmail.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("login.jwt.token");

        AuthResponseDto result = authService.login(loginDto);

        assertNotNull(result);
        assertEquals("login.jwt.token", result.getToken());
        assertEquals("shresth@gmail.com", result.getEmail());
        assertEquals("CUSTOMER", result.getRole());
    }

    @Test
    @DisplayName("Should throw exception when credentials are wrong")
    void login_wrongPassword_throwsException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(loginDto)
        );

        // user should never be fetched if auth fails
        verify(userRepository, never()).findByEmail(any());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should delegate credential verification to AuthenticationManager")
    void login_delegatesToAuthenticationManager() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(any())).thenReturn("token");

        authService.login(loginDto);

        // verify AuthenticationManager was called — not manual password check
        verify(authenticationManager).authenticate(
                argThat(auth ->
                        auth.getPrincipal().equals("shresth@gmail.com") &&
                                auth.getCredentials().equals("shresth123")
                )
        );
    }
}