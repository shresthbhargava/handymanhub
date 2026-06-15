package com.handymanhub.security;

import com.handymanhub.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extract Authorization header
        String authHeader = request.getHeader("Authorization");

        // 2. If no token — skip, let Security handle it
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract token (remove "Bearer " prefix)
        String token = authHeader.substring(7);

        // 4. Extract email from token
        String email = jwtUtil.extractEmail(token);

        // 5. If email found and not already authenticated
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Load user from database
            UserDetails userDetails = userRepository.findByEmail(email).orElse(null);

            // 7. Validate token
            if (userDetails != null && jwtUtil.isTokenValid(token, email)) {

                // 8. Create authentication object
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // 9. Set in security context — request is now authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 10. Continue to next filter / controller
        filterChain.doFilter(request, response);
    }
}