package com.handymanhub.controller;

import com.handymanhub.dto.request.BookingRequestDto;
import com.handymanhub.dto.response.BookingResponseDto;
import com.handymanhub.model.Booking;
import com.handymanhub.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;
import com.handymanhub.dto.response.PageResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
@Tag(name = "Skills", description = "Manage the trade skill catalogue — Electrician, Plumber, Mason etc.")
@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    @Operation(
            summary = "Get all workers with pagination",
            description = "Use ?page=0&size=10&sort=dailyRate,asc to paginate and sort"
    )
    @GetMapping
    public ResponseEntity<PageResponseDto<BookingResponseDto>> getAll(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(
                PageResponseDto.from(bookingService.getAllPaged(pageable))
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
    @Operation(summary = "Get all bookings for a customer")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BookingResponseDto>> getByCustomer(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(
                bookingService.getByCustomer(customerId).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }
    @Operation(summary = "Get bookings by status",
            description = "Valid values: PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<BookingResponseDto>> getByStatus(
            @PathVariable String status) {
        Booking.Status bookingStatus = Booking.Status.valueOf(status.toUpperCase());
        return ResponseEntity.ok(
                bookingService.getByStatus(bookingStatus).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "400", description = "Invalid status transition")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponseDto> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Booking.Status newStatus = Booking.Status.valueOf(status.toUpperCase());
        return ResponseEntity.ok(toDto(bookingService.updateStatus(id, newStatus)));
    }
    @Operation(
            summary = "Cancel a booking",
            description = "Can only cancel PENDING or CONFIRMED bookings. Cannot cancel IN_PROGRESS or COMPLETED."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking cancelled"),
            @ApiResponse(responseCode = "400", description = "Cannot cancel booking in current status")
    })
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