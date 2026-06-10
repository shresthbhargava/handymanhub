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

@RestController
@RequestMapping("/api/v1/workers")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @GetMapping
    public ResponseEntity<List<WorkerResponseDto>> getAll() {
        return ResponseEntity.ok(
                workerService.getAll().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkerResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(workerService.getById(id)));
    }

    @GetMapping("/available")
    public ResponseEntity<List<WorkerResponseDto>> getAvailableByPincode(
            @RequestParam String pincode) {
        return ResponseEntity.ok(
                workerService.getAvailableByPincode(pincode).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/contractor/{contractorId}")
    public ResponseEntity<List<WorkerResponseDto>> getByContractor(
            @PathVariable Long contractorId) {
        return ResponseEntity.ok(
                workerService.getByContractor(contractorId).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    @PostMapping
    public ResponseEntity<WorkerResponseDto> create(
            @Valid @RequestBody WorkerRequestDto dto) {
        Worker created = workerService.create(
                dto.getName(), dto.getPhone(), dto.getPincode(),
                dto.getDailyRate(), dto.getContractorId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }

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

    @PatchMapping("/{id}/availability")
    public ResponseEntity<WorkerResponseDto> toggleAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(workerService.toggleAvailability(id)));
    }

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