package com.handymanhub;

import com.handymanhub.exception.ResourceNotFoundException;
import com.handymanhub.model.Skill;
import com.handymanhub.model.Worker;
import com.handymanhub.model.WorkerSkill;
import com.handymanhub.repository.SkillRepository;
import com.handymanhub.repository.WorkerRepository;
import com.handymanhub.repository.WorkerSkillRepository;
import com.handymanhub.service.WorkerSkillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkerSkillServiceTest {

    @Mock WorkerSkillRepository workerSkillRepository;
    @Mock WorkerRepository workerRepository;
    @Mock SkillRepository skillRepository;

    @InjectMocks
    WorkerSkillService workerSkillService;

    private Worker worker;
    private Skill skill;
    private WorkerSkill workerSkill;

    @BeforeEach
    void setUp() {
        worker = Worker.builder()
                .name("Sunil Bijlee")
                .phone("9655005566")
                .pincode("110024")
                .dailyRate(new BigDecimal("1000.00"))
                .available(true)
                .build();

        skill = Skill.builder()
                .name("Electrician")
                .category("Electrical")
                .build();

        workerSkill = WorkerSkill.builder()
                .worker(worker)
                .skill(skill)
                .experienceYears(7)
                .build();
    }

    // ── Assign tests ──────────────────────────────────────────────

    @Test
    @DisplayName("Should assign skill to worker successfully")
    void assign_success() {
        when(workerRepository.findById(1L)).thenReturn(Optional.of(worker));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(workerSkillRepository.existsByWorkerIdAndSkillId(1L, 1L)).thenReturn(false);
        when(workerSkillRepository.save(any(WorkerSkill.class))).thenReturn(workerSkill);

        WorkerSkill result = workerSkillService.assign(1L, 1L, 7);

        assertNotNull(result);
        assertEquals("Sunil Bijlee", result.getWorker().getName());
        assertEquals("Electrician", result.getSkill().getName());
        assertEquals(7, result.getExperienceYears());

        verify(workerSkillRepository).save(any(WorkerSkill.class));
    }

    @Test
    @DisplayName("Should throw exception when worker not found")
    void assign_workerNotFound_throwsException() {
        when(workerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> workerSkillService.assign(99L, 1L, 5)
        );

        verify(workerSkillRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when skill not found")
    void assign_skillNotFound_throwsException() {
        when(workerRepository.findById(1L)).thenReturn(Optional.of(worker));
        when(skillRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> workerSkillService.assign(1L, 99L, 5)
        );

        verify(workerSkillRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when worker already has this skill")
    void assign_duplicateSkill_throwsException() {
        when(workerRepository.findById(1L)).thenReturn(Optional.of(worker));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(workerSkillRepository.existsByWorkerIdAndSkillId(1L, 1L)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> workerSkillService.assign(1L, 1L, 7)
        );

        assertTrue(ex.getMessage().contains("already has skill"));
        verify(workerSkillRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should assign skill with null experience years")
    void assign_withNullExperience_success() {
        when(workerRepository.findById(1L)).thenReturn(Optional.of(worker));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(workerSkillRepository.existsByWorkerIdAndSkillId(1L, 1L)).thenReturn(false);
        when(workerSkillRepository.save(any(WorkerSkill.class))).thenReturn(
                WorkerSkill.builder().worker(worker).skill(skill).experienceYears(null).build()
        );

        WorkerSkill result = workerSkillService.assign(1L, 1L, null);

        assertNotNull(result);
        assertNull(result.getExperienceYears());
    }

    // ── Remove tests ──────────────────────────────────────────────

    @Test
    @DisplayName("Should remove skill from worker successfully")
    void remove_success() {
        when(workerSkillRepository.findByWorkerIdAndSkillId(1L, 1L))
                .thenReturn(Optional.of(workerSkill));

        workerSkillService.remove(1L, 1L);

        verify(workerSkillRepository).delete(workerSkill);
    }

    @Test
    @DisplayName("Should throw exception when skill assignment not found")
    void remove_notFound_throwsException() {
        when(workerSkillRepository.findByWorkerIdAndSkillId(1L, 99L))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> workerSkillService.remove(1L, 99L)
        );

        assertTrue(ex.getMessage().contains("does not have skill"));
        verify(workerSkillRepository, never()).delete(any());
    }

    // ── Search tests ──────────────────────────────────────────────

    @Test
    @DisplayName("Should return available workers for skill and pincode")
    void search_returnsAvailableWorkers() {
        when(workerSkillRepository.findAvailableWorkersBySkillAndPincode(1L, "110024"))
                .thenReturn(List.of(workerSkill));

        List<WorkerSkill> result = workerSkillService.searchBySkillAndPincode(1L, "110024");

        assertEquals(1, result.size());
        assertEquals("Sunil Bijlee", result.get(0).getWorker().getName());
    }

    @Test
    @DisplayName("Should return empty list when no workers found")
    void search_noResults_returnsEmptyList() {
        when(workerSkillRepository.findAvailableWorkersBySkillAndPincode(1L, "999999"))
                .thenReturn(List.of());

        List<WorkerSkill> result = workerSkillService.searchBySkillAndPincode(1L, "999999");

        assertTrue(result.isEmpty());
        verify(workerSkillRepository).findAvailableWorkersBySkillAndPincode(1L, "999999");
    }
}