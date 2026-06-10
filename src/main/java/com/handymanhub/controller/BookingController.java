package com.handymanhub.controller;

import com.handymanhub.dto.request.BookingRequestDto;
import com.handymanhub.dto.response.BookingResponseDto;
import com.handymanhub.model.Booking;
import com.handymanhub.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAll() {
        return ResponseEntity.ok(
                bookingService.getAll().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(bookingService.getById(id)));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BookingResponseDto>> getByCustomer(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(
                bookingService.getByCustomer(customerId).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

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

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponseDto> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Booking.Status newStatus = Booking.Status.valueOf(status.toUpperCase());
        return ResponseEntity.ok(toDto(bookingService.updateStatus(id, newStatus)));
    }

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