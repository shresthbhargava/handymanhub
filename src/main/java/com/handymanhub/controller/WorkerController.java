package com.handymanhub.controller;

import com.handymanhub.dto.request.WorkerRequestDto;
import com.handymanhub.dto.response.WorkerResponseDto;
import com.handymanhub.model.Worker;
import com.handymanhub.service.WorkerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Workers", description = "Manage gig workers — register, search by location, toggle availability")
@RestController
@RequestMapping("/api/v1/workers")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }
    @Operation(summary = "Get all workers")
    @GetMapping
    public ResponseEntity<List<WorkerResponseDto>> getAll() {
        return ResponseEntity.ok(
                workerService.getAll().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }
    @Operation(summary = "Get worker by ID")
    @GetMapping("/{id}")
    public ResponseEntity<WorkerResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(workerService.getById(id)));
    }
    @Operation(
            summary = "Search available workers by pincode",
            description = "Returns only workers who are marked available in that pincode"
    )
    @GetMapping("/available")
    public ResponseEntity<List<WorkerResponseDto>> getAvailableByPincode(
            @RequestParam String pincode) {
        return ResponseEntity.ok(
                workerService.getAvailableByPincode(pincode).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }
    @Operation(summary = "Get all workers under a contractor")
    @GetMapping("/contractor/{contractorId}")
    public ResponseEntity<List<WorkerResponseDto>> getByContractor(
            @PathVariable Long contractorId) {
        return ResponseEntity.ok(
                workerService.getByContractor(contractorId).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }
    @Operation(
            summary = "Register a new worker",
            description = "contractorId is optional — omit it for independent workers"
    )
    @PostMapping
    public ResponseEntity<WorkerResponseDto> create(
            @Valid @RequestBody WorkerRequestDto dto) {
        Worker created = workerService.create(
                dto.getName(), dto.getPhone(), dto.getPincode(),
                dto.getDailyRate(), dto.getContractorId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }
    @Operation(summary = "Update worker details")
    @PutMapping("/{id}")
    public ResponseEntity<WorkerResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody WorkerRequestDto dto) {
        Worker updated = workerService.update(
                id, dto.getName(), dto.getPhone(), dto.getPincode(),
                dto.getDailyRate(), dto.getContractorId()
        );
        return ResponseEntity.ok(toDto(updated));
    }
    @Operation(
            summary = "Toggle worker availability",
            description = "Flips available status. true → false when worker starts a job, false → true when done."
    )
    @PatchMapping("/{id}/availability")
    public ResponseEntity<WorkerResponseDto> toggleAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(workerService.toggleAvailability(id)));
    }
    @Operation(summary = "Delete a worker")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private WorkerResponseDto toDto(Worker w) {
        return WorkerResponseDto.builder()
                .id(w.getId())
                .name(w.getName())
                .phone(w.getPhone())
                .pincode(w.getPincode())
                .dailyRate(w.getDailyRate())
                .available(w.getAvailable())
                .contractorId(w.getContractor() != null ? w.getContractor().getId() : null)
                .contractorName(w.getContractor() != null ? w.getContractor().getName() : null)
                .createdAt(w.getCreatedAt())
                .build();
    }
}