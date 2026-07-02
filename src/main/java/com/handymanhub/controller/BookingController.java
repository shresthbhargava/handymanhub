package com.handymanhub.controller;

import com.handymanhub.dto.request.BookingRequestDto;
import com.handymanhub.dto.response.BookingResponseDto;
import com.handymanhub.model.Booking;
import com.handymanhub.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// WHAT CHANGED:
//
// 1. GET / — added optional ?status= filter.
//    ?status=PENDING → only pending bookings (replaces the old /status/{status} endpoint)
//    No param → returns all bookings (same as before)
//
// 2. GET /customer/{id} — now paginated (was unbounded List)
//
// 3. REMOVED: GET /status/{status} — replaced by ?status= filter on main GET
//    One endpoint with an optional filter is cleaner than two endpoints.
//    This is the "filter over sub-resource" pattern.
//
// WHY REMOVE /status/{status}?
//   Before: GET /bookings/status/PENDING
//   After:  GET /bookings?status=PENDING
//   Same result, but the second approach is more RESTful because:
//   - It's the same resource (bookings) with a filter
//   - Sorting and pagination work the same way: ?status=PENDING&sort=createdAt,desc&page=1
//   - With the sub-resource approach, you'd need to add pagination/sorting
//     to EVERY sub-endpoint separately
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Tag(name = "Bookings", description = "Manage service bookings — create, track status, cancel")
@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(
            summary = "Get all bookings with pagination, sorting, and status filter",
            description = """
            Examples:
            - ?page=0&size=10 → first 10 bookings
            - ?status=PENDING → only pending bookings
            - ?status=COMPLETED&sort=scheduledDate,asc → completed bookings sorted by date
            - ?sort=createdAt,desc → newest first
            """
    )
    @GetMapping
    public ResponseEntity<com.handymanhub.dto.response.PageResponseDto<BookingResponseDto>> getAll(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        // Convert string to enum (or null if not provided)
        Booking.Status statusEnum = null;
        if (status != null && !status.isBlank()) {
            statusEnum = Booking.Status.valueOf(status.toUpperCase());
        }

        return ResponseEntity.ok(
                com.handymanhub.dto.response.PageResponseDto.from(
                        bookingService.getAllFiltered(statusEnum, pageable))
        );
    }

    @Operation(summary = "Get booking by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking found"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(bookingService.getById(id)));
    }

    @Operation(
            summary = "Get all bookings for a customer (paginated)",
            description = "Use ?page=0&size=10 for pagination"
    )
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<com.handymanhub.dto.response.PageResponseDto<BookingResponseDto>> getByCustomer(
            @PathVariable Long customerId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(
                com.handymanhub.dto.response.PageResponseDto.from(
                        bookingService.getByCustomerPaged(customerId, pageable))
        );
    }

    @Operation(
            summary = "Create a new booking",
            description = """
            Rules:
            - Provide workerId OR contractorId — never both, never neither
            - Worker must be available
            - Worker must not already be booked on that date
            - Contractor must be verified
            - Scheduled date cannot be in the past
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created with status PENDING"),
            @ApiResponse(responseCode = "400", description = "Business rule violation"),
            @ApiResponse(responseCode = "404", description = "Customer, worker, contractor or skill not found")
    })
    @PostMapping
    public ResponseEntity<BookingResponseDto> create(
            @Valid @RequestBody BookingRequestDto dto) {
        Booking created = bookingService.create(
                dto.getCustomerId(),
                dto.getWorkerId(),
                dto.getContractorId(),
                dto.getSkillId(),
                dto.getScheduledDate(),
                dto.getDurationDays(),
                dto.getAddress(),
                dto.getNotes()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }

    @Operation(
            summary = "Update booking status",
            description = "Valid transitions: PENDING→CONFIRMED, CONFIRMED→IN_PROGRESS, IN_PROGRESS→COMPLETED. No skipping, no going back."
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponseDto> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Booking.Status newStatus = Booking.Status.valueOf(status.toUpperCase());
        return ResponseEntity.ok(toDto(bookingService.updateStatus(id, newStatus)));
    }

    @Operation(
            summary = "Cancel a booking",
            description = "Can only cancel PENDING or CONFIRMED bookings."
    )
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponseDto> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(bookingService.cancel(id)));
    }

    private BookingResponseDto toDto(Booking b) {
        return BookingResponseDto.builder()
                .id(b.getId())
                .customerId(b.getCustomer().getId())
                .customerName(b.getCustomer().getName())
                .workerId(b.getWorker() != null ? b.getWorker().getId() : null)
                .workerName(b.getWorker() != null ? b.getWorker().getName() : null)
                .contractorId(b.getContractor() != null ? b.getContractor().getId() : null)
                .contractorName(b.getContractor() != null ? b.getContractor().getName() : null)
                .skillId(b.getSkill().getId())
                .skillName(b.getSkill().getName())
                .scheduledDate(b.getScheduledDate())
                .durationDays(b.getDurationDays())
                .status(b.getStatus())
                .address(b.getAddress())
                .notes(b.getNotes())
                .createdAt(b.getCreatedAt())
                .build();
    }
}