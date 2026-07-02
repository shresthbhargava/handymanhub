package com.handymanhub.controller;

import com.handymanhub.dto.response.AdminStatsResponseDto;
import com.handymanhub.dto.response.BookingResponseDto;
import com.handymanhub.dto.response.UserResponseDto;
import com.handymanhub.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// ALL ENDPOINTS HERE REQUIRE ROLE_ADMIN.
// Enforced by SecurityConfig: .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
//
// WHY /admin/** AND NOT SCATTERED ACROSS OTHER CONTROLLERS:
// Clean URL structure. Frontend can prefix all admin calls with /admin.
// Also makes SecurityConfig simpler — one rule covers everything.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Tag(name = "Admin", description = "Platform statistics and user management (ADMIN only)")
@RestController
@RequestMapping("/api/v1/admin")
@SecurityRequirement(name = "bearerAuth")  // Shows lock icon in Swagger
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(
            summary = "Get platform statistics",
            description = "Returns all key metrics: bookings by status, total workers/customers/contractors, estimated revenue. Single call for the entire admin dashboard."
    )
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponseDto> getStats() {
        return ResponseEntity.ok(adminService.getPlatformStats());
    }

    @Operation(
            summary = "List all registered users",
            description = "Returns paginated list of all users (from auth table). Password is never included."
    )
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponseDto>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getAllUsers(PageRequest.of(page, size)));
    }
}