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

@RestController
@RequestMapping("/api/v1/contractors")
public class ContractorController {

    private final ContractorService contractorService;

    public ContractorController(ContractorService contractorService) {
        this.contractorService = contractorService;
    }

    @GetMapping
    public ResponseEntity<List<ContractorResponseDto>> getAll() {
        return ResponseEntity.ok(
                contractorService.getAll().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractorResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(contractorService.getById(id)));
    }

    @GetMapping("/verified")
    public ResponseEntity<List<ContractorResponseDto>> getVerified() {
        return ResponseEntity.ok(
                contractorService.getVerified().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/pincode/{pincode}")
    public ResponseEntity<List<ContractorResponseDto>> getByPincode(@PathVariable String pincode) {
        return ResponseEntity.ok(
                contractorService.getByPincode(pincode).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    @PostMapping
    public ResponseEntity<ContractorResponseDto> create(@Valid @RequestBody ContractorRequestDto dto) {
        Contractor created = contractorService.create(
                dto.getName(), dto.getPhone(), dto.getEmail(),
                dto.getPincode(), dto.getCompanyName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContractorResponseDto> update(@PathVariable Long id,
                                                        @Valid @RequestBody ContractorRequestDto dto) {
        Contractor updated = contractorService.update(
                id, dto.getName(), dto.getPhone(), dto.getEmail(),
                dto.getPincode(), dto.getCompanyName()
        );
        return ResponseEntity.ok(toDto(updated));
    }

    @PatchMapping("/{id}/verify")
    public ResponseEntity<ContractorResponseDto> verify(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(contractorService.verify(id)));
    }

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