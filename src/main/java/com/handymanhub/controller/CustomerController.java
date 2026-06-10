package com.handymanhub.controller;

import com.handymanhub.dto.request.CustomerRequestDto;
import com.handymanhub.dto.response.CustomerResponseDto;
import com.handymanhub.model.Customer;
import com.handymanhub.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDto>> getAll() {
        return ResponseEntity.ok(customerService.getAll().stream()
                .map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(customerService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDto> create(
            @Valid @RequestBody CustomerRequestDto dto) {
        Customer created = customerService.create(
                dto.getName(), dto.getPhone(), dto.getEmail(),
                dto.getAddress(), dto.getPincode());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDto dto) {
        Customer updated = customerService.update(
                id, dto.getName(), dto.getPhone(), dto.getEmail(),
                dto.getAddress(), dto.getPincode());
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private CustomerResponseDto toDto(Customer c) {
        return CustomerResponseDto.builder()
                .id(c.getId()).name(c.getName()).phone(c.getPhone())
                .email(c.getEmail()).address(c.getAddress())
                .pincode(c.getPincode()).createdAt(c.getCreatedAt())
                .build();
    }
}