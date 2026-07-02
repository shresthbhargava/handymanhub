package com.handymanhub.security;

import com.handymanhub.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// WHAT CHANGED:
//
// The old config had:  .requestMatchers("/api/v1/auth/**").permitAll()
// This allowed EVERYTHING under /auth — including /logout.
// But /logout MUST be protected (we need to know WHO is logging out).
//
// THE FIX: Replace the wildcard with specific public endpoints,
// and let /auth/logout fall through to .anyRequest().authenticated()
//
// BEFORE:
//   /api/v1/auth/**     → permitAll  (too broad — /logout was unprotected!)
//
// AFTER:
//   POST /auth/register  → permitAll  (anyone can create account)
//   POST /auth/login     → permitAll  (anyone can login)
//   POST /auth/refresh   → permitAll  (access token is expired, that's the point)
//   POST /auth/logout    → authenticated  (falls through to anyRequest)
//   GET  /auth/**        → would be 405 (no GET handlers exist, harmless)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserRepository userRepository;

    public SecurityConfig(JwtFilter jwtFilter, UserRepository userRepository) {
        this.jwtFilter = jwtFilter;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // CORS preflight — browsers send OPTIONS before every cross-origin request
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ── CHANGED: specific auth endpoints instead of wildcard ──
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh").permitAll()
                        // NOTE: /api/v1/auth/logout is NOT here → falls to anyRequest().authenticated()

                        // Swagger docs (public for developer convenience)
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()

                        // Actuator health (public — Render needs this to detect if app is alive)
                        .requestMatchers("/actuator/health").permitAll()
                        // Actuator info is less sensitive but still not for anonymous users.
                        // Falls through to anyRequest().authenticated() if you want it protected.

                        // Public read endpoints (anyone can browse)
                        .requestMatchers(HttpMethod.GET, "/api/v1/skills/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/workers/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/contractors/verified").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/bookings/worker/**").permitAll()

                        // Admin-only endpoints
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/contractors/*/verify").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")

                        // Everything else (including /auth/logout, /dashboard, bookings, etc.)
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"No token provided or token is invalid\"}"
                            );
                        })
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}