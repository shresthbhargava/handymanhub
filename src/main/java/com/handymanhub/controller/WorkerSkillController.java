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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Worker Skills", description = "Assign trade skills to workers and search workers by skill + location")
@RestController
public class WorkerSkillController {

    private final WorkerSkillService workerSkillService;

    public WorkerSkillController(WorkerSkillService workerSkillService) {
        this.workerSkillService = workerSkillService;
    }
    @Operation(summary = "Get all skills for a worker")
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
    @Operation(
            summary = "Assign a skill to a worker",
            description = "experienceYears is optional in the request body. A worker cannot have the same skill twice."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Skill assigned"),
            @ApiResponse(responseCode = "400", description = "Worker already has this skill"),
            @ApiResponse(responseCode = "404", description = "Worker or skill not found")
    })
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
    @Operation(summary = "Remove a skill from a worker")
    // DELETE /api/v1/workers/1/skills/2
    @DeleteMapping("/api/v1/workers/{workerId}/skills/{skillId}")
    public ResponseEntity<Void> remove(
            @PathVariable Long workerId,
            @PathVariable Long skillId) {
        workerSkillService.remove(workerId, skillId);
        return ResponseEntity.noContent().build();
    }
    @Operation(
            summary = "Search available workers by skill and pincode",
            description = "The core search endpoint. Returns workers who have the skill, are available, and are in the given pincode."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of matching workers — empty list if none found")
    })
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