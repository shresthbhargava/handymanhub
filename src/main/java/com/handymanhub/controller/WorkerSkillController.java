package com.handymanhub.controller;

import com.handymanhub.dto.request.WorkerSkillRequestDto;
import com.handymanhub.dto.response.WorkerSkillResponseDto;
import com.handymanhub.model.WorkerSkill;
import com.handymanhub.service.WorkerSkillService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class WorkerSkillController {

    private final WorkerSkillService workerSkillService;

    public WorkerSkillController(WorkerSkillService workerSkillService) {
        this.workerSkillService = workerSkillService;
    }

    // GET /api/v1/workers/1/skills
    @GetMapping("/api/v1/workers/{workerId}/skills")
    public ResponseEntity<List<WorkerSkillResponseDto>> getWorkerSkills(
            @PathVariable Long workerId) {
        return ResponseEntity.ok(
                workerSkillService.getWorkerSkills(workerId).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    // POST /api/v1/workers/1/skills/2
    @PostMapping("/api/v1/workers/{workerId}/skills/{skillId}")
    public ResponseEntity<WorkerSkillResponseDto> assign(
            @PathVariable Long workerId,
            @PathVariable Long skillId,
            @Valid @RequestBody(required = false) WorkerSkillRequestDto dto) {
        Integer experience = dto != null ? dto.getExperienceYears() : null;
        WorkerSkill ws = workerSkillService.assign(workerId, skillId, experience);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(ws));
    }

    // DELETE /api/v1/workers/1/skills/2
    @DeleteMapping("/api/v1/workers/{workerId}/skills/{skillId}")
    public ResponseEntity<Void> remove(
            @PathVariable Long workerId,
            @PathVariable Long skillId) {
        workerSkillService.remove(workerId, skillId);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/workers/search?skillId=1&pincode=110024
    @GetMapping("/api/v1/workers/search")
    public ResponseEntity<List<WorkerSkillResponseDto>> search(
            @RequestParam Long skillId,
            @RequestParam String pincode) {
        return ResponseEntity.ok(
                workerSkillService.searchBySkillAndPincode(skillId, pincode).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    private WorkerSkillResponseDto toDto(WorkerSkill ws) {
        return WorkerSkillResponseDto.builder()
                .id(ws.getId())
                .workerId(ws.getWorker().getId())
                .workerName(ws.getWorker().getName())
                .workerPincode(ws.getWorker().getPincode())
                .skillId(ws.getSkill().getId())
                .skillName(ws.getSkill().getName())
                .skillCategory(ws.getSkill().getCategory())
                .experienceYears(ws.getExperienceYears())
                .workerAvailable(ws.getWorker().getAvailable())
                .build();
    }
}