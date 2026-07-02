package com.handymanhub.controller;

import com.handymanhub.dto.response.BookingResponseDto;
import com.handymanhub.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// ADD THIS ENDPOINT to your existing WorkerController.
// Don't create a separate file — just add the method.
//
// If your WorkerController is in a different package, adjust the
// import for AdminService accordingly. The logic reuses AdminService
// because it's a read-only query that fits there.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Tag(name = "Worker Bookings", description = "Worker's own booking history")
@RestController
@RequestMapping("/api/v1")
public class WorkerBookingController {

    private final AdminService adminService;

    public WorkerBookingController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(
            summary = "Get bookings for a specific worker",
            description = "Returns paginated list of bookings assigned to this worker. Useful for worker dashboard."
    )
    @GetMapping("/bookings/worker/{workerId}")
    public ResponseEntity<Page<BookingResponseDto>> getWorkerBookings(
            @PathVariable Long workerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                adminService.getWorkerBookings(workerId, PageRequest.of(page, size)));
    }
}

// ━━━ ALTERNATIVE: ADD TO YOUR EXISTING WorkerController ━━━
// If you have a WorkerController.java, just add this method inside it:
//
//     @Autowired
//     private AdminService adminService;
//
//     @GetMapping("/bookings/worker/{workerId}")
//     public ResponseEntity<Page<BookingResponseDto>> getWorkerBookings(
//             @PathVariable Long workerId,
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size) {
//         return ResponseEntity.ok(
//                 adminService.getWorkerBookings(workerId, PageRequest.of(page, size)));
//     }
//
// Then DELETE this WorkerBookingController file entirely.
// One controller per resource is cleaner.