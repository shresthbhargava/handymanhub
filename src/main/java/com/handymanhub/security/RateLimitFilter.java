package com.handymanhub.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// WHAT THIS DOES:
//   Intercepts requests to /auth/login, /auth/register, /auth/refresh.
//   Tracks how many times each IP has called these endpoints.
//   If an IP exceeds the limit → returns 429 without touching the controller.
//
// WHY A FILTER AND NOT AN INTERCEPTOR/AOP?
//   Filter runs BEFORE Spring Security. An attacker sending 10,000
//   login attempts should be stopped at the door — we don't want
//   Spring Security running password hashing (BCrypt is intentionally
//   slow) on every attempt. The filter rejects them immediately.
//
// THREAD SAFETY:
//   ConcurrentHashMap — each IP gets its own bucket.
//   AtomicInteger inside each bucket — thread-safe counter.
//   ConcurrentHashMap.compute() — atomic per key.
//   So even if 100 requests from the same IP arrive simultaneously,
//   the counter increments correctly.
//
// PRODUCTION NOTE:
//   In production with multiple server instances, use Redis + Bucket4j
//   instead of in-memory. In-memory only works for a single instance.
//   For your Render deployment (single instance), this is perfectly fine.
//
// MEMORY LEAK PREVENTION:
//   Old bucket entries stay in the map forever. For a portfolio project
//   this is fine (thousands of IPs × ~50 bytes = negligible).
//   In production, you'd add a @Scheduled cleanup that removes
//   expired entries every hour.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // Run BEFORE Spring Security's filter chain (which has default order)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    // Key = client IP, Value = request bucket for this window
    private final ConcurrentHashMap<String, RateBucket> buckets = new ConcurrentHashMap<>();

    // Configurable from application.yml
    @Value("${rate-limit.max-requests:5}")
    private int maxRequests;

    @Value("${rate-limit.window-minutes:15}")
    private int windowMinutes;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Only rate limit authentication endpoints — skip everything else
        if (!shouldRateLimit(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientId = resolveClientIp(request);
        long windowMs = (long) windowMinutes * 60 * 1000;

        // ── Get or create the bucket for this IP ──────────────────
        // ConcurrentHashMap.compute() is ATOMIC per key.
        // Only one thread's lambda executes at a time for a given IP.
        RateBucket bucket = buckets.compute(clientId, (key, existing) -> {
            if (existing == null || existing.isWindowExpired(windowMs)) {
                // No bucket yet, or window expired → fresh bucket
                return new RateBucket(maxRequests);
            }
            // Window still active → reuse existing bucket
            return existing;
        });

        // ── Try to acquire a permit ───────────────────────────────
        // AtomicInteger.incrementAndGet() is atomic — safe for concurrent requests
        if (bucket.tryAcquire()) {
            // Request allowed — add standard rate limit headers
            // These headers are industry standard (GitHub, Stripe, Cloudflare all use them)
            response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequests));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(bucket.getRemaining()));
            filterChain.doFilter(request, response);
        } else {
            // Request BLOCKED — too many attempts
            log.warn("Rate limit exceeded for IP={} on path={} (limit={}/{})",
                    clientId, path, maxRequests, windowMinutes + "min");

            response.setStatus(429);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Tell the client how long to wait before retrying
            long retryAfterSeconds = (bucket.getWindowStartMs() + windowMs) / 1000
                    - System.currentTimeMillis() / 1000;
            response.setHeader("Retry-After", String.valueOf(Math.max(1, retryAfterSeconds)));
            response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequests));
            response.setHeader("X-RateLimit-Remaining", "0");

            // Structured JSON error (matches your global exception handler style)
            Map<String, Object> errorBody = Map.of(
                    "status", 429,
                    "error", "Too Many Requests",
                    "message", "Too many attempts. Try again in " + Math.max(1, retryAfterSeconds) + " seconds."
            );
            response.getWriter().write(objectMapper.writeValueAsString(errorBody));
        }
    }

    // ── Which endpoints to rate limit ────────────────────────────
    // Be specific — don't rate limit /auth/refresh too aggressively
    // because the frontend calls it automatically every 15 minutes.
    private boolean shouldRateLimit(String path) {
        return path.equals("/api/v1/auth/login") ||
               path.equals("/api/v1/auth/register") ||
               path.equals("/api/v1/auth/refresh");
    }

    // ── Extract the real client IP ───────────────────────────────
    // On Render (or any reverse proxy), the client IP is in
    // X-Forwarded-For header, not in request.getRemoteAddr().
    // request.getRemoteAddr() would give you the proxy's IP,
    // and ALL users would share the same rate limit bucket.
    // X-Forwarded-For format: "real-ip, proxy1-ip, proxy2-ip"
    // We take the first one — that's the original client.
    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        // Fallback for local development (no proxy)
        return request.getRemoteAddr();
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // INNER CLASS: The "bucket" that tracks requests per window
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    static class RateBucket {
        private final int maxRequests;
        private final long windowStartMs;
        private final AtomicInteger count;

        RateBucket(int maxRequests) {
            this.maxRequests = maxRequests;
            this.windowStartMs = System.currentTimeMillis();
            this.count = new AtomicInteger(0);
        }

        // Returns true if request is within limit, false if exceeded
        boolean tryAcquire() {
            return count.incrementAndGet() <= maxRequests;
        }

        // How many requests are left in this window
        int getRemaining() {
            return Math.max(0, maxRequests - count.get());
        }

        // Has this time window expired?
        boolean isWindowExpired(long windowDurationMs) {
            return (System.currentTimeMillis() - windowStartMs) >= windowDurationMs;
        }

        long getWindowStartMs() {
            return windowStartMs;
        }
    }
}