package com.handymanhub.controller;

import com.handymanhub.dto.request.ContractorRequestDto;
import com.handymanhub.dto.response.ContractorResponseDto;
import com.handymanhub.model.Contractor;
import com.handymanhub.service.ContractorService;
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
@Tag(name = "Contractors", description = "Manage contractors — verified contractors can be booked for team jobs")
@RestController
@RequestMapping("/api/v1/contractors")
public class ContractorController {

    private final ContractorService contractorService;

    public ContractorController(ContractorService contractorService) {
        this.contractorService = contractorService;
    }
    @Operation(summary = "Get all contractors")
    @GetMapping
    public ResponseEntity<List<ContractorResponseDto>> getAll() {
        return ResponseEntity.ok(
                contractorService.getAll().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }
    @Operation(summary = "Get contractor by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ContractorResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(contractorService.getById(id)));
    }
    @Operation(summary = "Get all verified contractors", description = "Only verified contractors appear to customers for booking")
    @GetMapping("/verified")
    public ResponseEntity<List<ContractorResponseDto>> getVerified() {
        return ResponseEntity.ok(
                contractorService.getVerified().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }
    @Operation(summary = "Get contractors by pincode")
    @GetMapping("/pincode/{pincode}")
    public ResponseEntity<List<ContractorResponseDto>> getByPincode(@PathVariable String pincode) {
        return ResponseEntity.ok(
                contractorService.getByPincode(pincode).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }
    @Operation(summary = "Register a new contractor")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contractor registered"),
            @ApiResponse(responseCode = "400", description = "Phone or email already registered")
    })
    @PostMapping
    public ResponseEntity<ContractorResponseDto> create(@Valid @RequestBody ContractorRequestDto dto) {
        Contractor created = contractorService.create(
                dto.getName(), dto.getPhone(), dto.getEmail(),
                dto.getPincode(), dto.getCompanyName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }
    @Operation(summary = "Update contractor details")
    @PutMapping("/{id}")
    public ResponseEntity<ContractorResponseDto> update(@PathVariable Long id,
                                                        @Valid @RequestBody ContractorRequestDto dto) {
        Contractor updated = contractorService.update(
                id, dto.getName(), dto.getPhone(), dto.getEmail(),
                dto.getPincode(), dto.getCompanyName()
        );
        return ResponseEntity.ok(toDto(updated));
    }
    @Operation(
            summary = "Verify a contractor",
            description = "Admin action — marks a contractor as verified. Only verified contractors can be booked by customers."
    )
    @ApiResponse(responseCode = "200", description = "Contractor verified successfully")
    @PatchMapping("/{id}/verify")
    public ResponseEntity<ContractorResponseDto> verify(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(contractorService.verify(id)));
    }
    @Operation(summary = "Delete a contractor")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contractorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ContractorResponseDto toDto(Contractor c) {
        return ContractorResponseDto.builder()
                .id(c.getId())
                .name(c.getName())
                .phone(c.getPhone())
                .email(c.getEmail())
                .pincode(c.getPincode())
                .companyName(c.getCompanyName())
                .verified(c.getVerified())
                .workerCount(0)
                .createdAt(c.getCreatedAt())
                .build();
    }
}