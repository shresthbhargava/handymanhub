package com.handymanhub.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// WHAT THIS DOES:
//   Spring Boot Actuator auto-configures a DB health check.
//   But it just says "UP" or "DOWN". This custom indicator
//   adds ACTUAL DATA — how many bookings exist, how many workers.
//
//   When you call GET /actuator/health, you'll now see:
//   {
//     "status": "UP",
//     "components": {
//       "db": { "status": "UP" },         ← auto-configured
//       "apiMetrics": {                    ← THIS custom one
//         "status": "UP",
//         "details": {
//           "totalBookings": 12,
//           "totalWorkers": 8,
//           "totalCustomers": 5
//         }
//       }
//     }
//   }
//
// WHY: Render's free tier requires a /health endpoint that returns 200.
// Actuator gives you this for free. The custom indicator shows you
// understand how to EXTEND Actuator, not just use defaults.
//
// INTERVIEW ANSWER: "I added a custom HealthIndicator that runs
// a COUNT query to verify the database is not just connected,
// but actually responding to queries."
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Component
public class ApiHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    public ApiHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try {
            // Run simple COUNT queries to prove DB is actually responding
            Long bookings = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM bookings", Long.class);
            Long workers = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM workers", Long.class);
            Long customers = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM customers", Long.class);

            return Health.up()
                    .withDetails(Map.of(
                            "totalBookings", bookings != null ? bookings : 0,
                            "totalWorkers", workers != null ? workers : 0,
                            "totalCustomers", customers != null ? customers : 0
                    ))
                    .build();

        } catch (Exception e) {
            // If any query fails, the health is DOWN
            // This triggers Render to restart your container
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}