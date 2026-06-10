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

@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public ResponseEntity<List<SkillResponseDto>> getAll() {
        List<SkillResponseDto> skills = skillService.getAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(skills);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toDto(skillService.getById(id)));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<SkillResponseDto>> getByCategory(@PathVariable String category) {
        List<SkillResponseDto> skills = skillService.getByCategory(category)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(skills);
    }

    @PostMapping
    public ResponseEntity<SkillResponseDto> create(@Valid @RequestBody SkillRequestDto dto) {
        Skill created = skillService.create(dto.getName(), dto.getCategory(), dto.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillResponseDto> update(@PathVariable Long id,
                                                   @Valid @RequestBody SkillRequestDto dto) {
        Skill updated = skillService.update(id, dto.getName(), dto.getCategory(), dto.getDescription());
        return ResponseEntity.ok(toDto(updated));
    }

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