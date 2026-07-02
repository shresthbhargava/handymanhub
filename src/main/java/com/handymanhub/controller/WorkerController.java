package com.handymanhub.controller;

import com.handymanhub.dto.request.WorkerRequestDto;
import com.handymanhub.dto.response.WorkerResponseDto;
import com.handymanhub.model.Worker;
import com.handymanhub.service.WorkerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// WHAT CHANGED from your original WorkerController:
//
// 1. GET / — added optional ?available= and ?pincode= filters
//    Calls workerService.getAllFiltered() instead of getAllPaged()
//
// 2. GET /available?pincode= — now returns PageResponseDto (was List)
//
// 3. GET /contractor/{id} — now returns PageResponseDto (was List)
//
// 4. toDto() — UNCHANGED. Rating is fetched separately via
//    GET /workers/{id}/rating to avoid N+1 query problem.
//    (See Pass 3 explanation below)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

@Tag(name = "Workers", description = "Manage gig workers — register, search, paginate, filter")
@RestController
@RequestMapping("/api/v1/workers")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @Operation(
            summary = "Get all workers with pagination, sorting, and filtering",
            description = """
            Examples:
            - ?page=0&size=10 → first 10 workers
            - ?sort=dailyRate,desc → highest paid first
            - ?available=true → only available workers
            - ?pincode=110024 → workers in that area
            - ?available=true&pincode=110024&sort=dailyRate,asc → combine all
            """
    )
    @GetMapping
    public ResponseEntity<com.handymanhub.dto.response.PageResponseDto<WorkerResponseDto>> getAll(
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) String pincode,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return ResponseEntity.ok(
                com.handymanhub.dto.response.PageResponseDto.from(
                        workerService.getAllFiltered(available, pincode, pageable)
                                .map(this::toDto))
        );
    }

    @Operation(summary = "Get worker by ID")
    @GetMapping("/{id}")
    public ResponseEntity<WorkerResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(workerService.getById(id)));
    }

    @Operation(
            summary = "Search available workers by pincode (paginated)",
            description = "Use ?page=0&size=10 for pagination"
    )
    @GetMapping("/available")
    public ResponseEntity<com.handymanhub.dto.response.PageResponseDto<WorkerResponseDto>> getAvailableByPincode(
            @RequestParam String pincode,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return ResponseEntity.ok(
                com.handymanhub.dto.response.PageResponseDto.from(
                        workerService.getAvailableByPincodePaged(pincode, pageable)
                                .map(this::toDto))
        );
    }

    @Operation(
            summary = "Get all workers under a contractor (paginated)",
            description = "Use ?page=0&size=10 for pagination"
    )
    @GetMapping("/contractor/{contractorId}")
    public ResponseEntity<com.handymanhub.dto.response.PageResponseDto<WorkerResponseDto>> getByContractor(
            @PathVariable Long contractorId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return ResponseEntity.ok(
                com.handymanhub.dto.response.PageResponseDto.from(
                        workerService.getByContractorPaged(contractorId, pageable)
                                .map(this::toDto))
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
            description = "Flips available status."
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