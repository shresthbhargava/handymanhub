package com.handymanhub.controller;

import com.handymanhub.dto.request.SkillRequestDto;
import com.handymanhub.dto.response.SkillResponseDto;
import com.handymanhub.model.Skill;
import com.handymanhub.service.SkillService;
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
@Tag(name = "Skills", description = "Manage the trade skill catalogue — Electrician, Plumber, Mason etc.")
@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }
    @Operation(summary = "Get all skills", description = "Returns all skills ordered by category and name")
    @GetMapping
    public ResponseEntity<List<SkillResponseDto>> getAll() {
        List<SkillResponseDto> skills = skillService.getAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(skills);
    }
    @Operation(summary = "Get skill by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Skill found"),
            @ApiResponse(responseCode = "404", description = "Skill not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SkillResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(skillService.getById(id)));
    }
    @Operation(summary = "Get skills by category", description = "Example categories: Electrical, Plumbing, Civil, Domestic")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<SkillResponseDto>> getByCategory(@PathVariable String category) {
        List<SkillResponseDto> skills = skillService.getByCategory(category)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(skills);
    }
    @Operation(summary = "Create a new skill")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Skill created"),
            @ApiResponse(responseCode = "400", description = "Skill name already exists or invalid input")
    })
    @PostMapping
    public ResponseEntity<SkillResponseDto> create(@Valid @RequestBody SkillRequestDto dto) {
        Skill created = skillService.create(dto.getName(), dto.getCategory(), dto.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }
    @Operation(summary = "Update an existing skill")
    @PutMapping("/{id}")
    public ResponseEntity<SkillResponseDto> update(@PathVariable Long id,
                                                   @Valid @RequestBody SkillRequestDto dto) {
        Skill updated = skillService.update(id, dto.getName(), dto.getCategory(), dto.getDescription());
        return ResponseEntity.ok(toDto(updated));
    }
    @Operation(summary = "Delete a skill")
    @ApiResponse(responseCode = "204", description = "Skill deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        skillService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private SkillResponseDto toDto(Skill skill) {
        return SkillResponseDto.builder()
                .id(skill.getId())
                .name(skill.getName())
                .category(skill.getCategory())
                .description(skill.getDescription())
                .build();
    }
}