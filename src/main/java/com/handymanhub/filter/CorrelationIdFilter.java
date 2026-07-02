package com.handymanhub.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

// Order(2) = runs AFTER RateLimitFilter(1) but BEFORE Spring Security
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID = "correlationId";
    public static final String CORRELATION_HEADER = "X-Correlation-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Check if client sent a correlation ID (useful when frontend includes it)
        String correlationId = request.getHeader(CORRELATION_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        }

        // Put in MDC so every log statement picks it up automatically
        MDC.put(CORRELATION_ID, correlationId);

        // Return it in response so client can use it for support tickets
        response.setHeader(CORRELATION_HEADER, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // CRITICAL: always clean up MDC to prevent leaks between requests
            // On a server, request threads are reused via thread pool.
            // Without this cleanup, the next request on the same thread
            // would inherit the previous request's correlation ID.
            MDC.remove(CORRELATION_ID);
        }
    }
}