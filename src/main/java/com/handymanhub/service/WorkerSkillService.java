package com.handymanhub.service;

import com.handymanhub.exception.ResourceNotFoundException;
import com.handymanhub.model.Skill;
import com.handymanhub.model.Worker;
import com.handymanhub.model.WorkerSkill;
import com.handymanhub.repository.SkillRepository;
import com.handymanhub.repository.WorkerRepository;
import com.handymanhub.repository.WorkerSkillRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkerSkillService {

    private static final Logger log = LoggerFactory.getLogger(WorkerSkillService.class);

    private final WorkerSkillRepository workerSkillRepository;
    private final WorkerRepository workerRepository;
    private final SkillRepository skillRepository;

    public WorkerSkillService(WorkerSkillRepository workerSkillRepository,
                              WorkerRepository workerRepository,
                              SkillRepository skillRepository) {
        this.workerSkillRepository = workerSkillRepository;
        this.workerRepository = workerRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<WorkerSkill> getWorkerSkills(Long workerId) {
        // confirm worker exists first
        workerRepository.findById(workerId)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", workerId));
        return workerSkillRepository.findByWorkerId(workerId);
    }

    @Transactional(readOnly = true)
    public List<WorkerSkill> searchBySkillAndPincode(Long skillId, String pincode) {
        log.debug("Searching workers with skillId={} in pincode={}", skillId, pincode);
        return workerSkillRepository.findAvailableWorkersBySkillAndPincode(skillId, pincode);
    }

    @Transactional
    public WorkerSkill assign(Long workerId, Long skillId, Integer experienceYears) {
        log.info("Assigning skillId={} to workerId={}", skillId, workerId);

        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", workerId));

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", skillId));

        if (workerSkillRepository.existsByWorkerIdAndSkillId(workerId, skillId)) {
            throw new IllegalArgumentException(
                    "Worker id=" + workerId + " already has skill id=" + skillId);
        }

        WorkerSkill ws = WorkerSkill.builder()
                .worker(worker)
                .skill(skill)
                .experienceYears(experienceYears)
                .build();

        WorkerSkill saved = workerSkillRepository.save(ws);
        log.info("Skill assigned — workerId={} skillId={} experience={}yrs",
                workerId, skillId, experienceYears);
        return saved;
    }

    @Transactional
    public void remove(Long workerId, Long skillId) {
        log.info("Removing skillId={} from workerId={}", skillId, workerId);

        WorkerSkill ws = workerSkillRepository
                .findByWorkerIdAndSkillId(workerId, skillId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Worker id=" + workerId + " does not have skill id=" + skillId));

        workerSkillRepository.delete(ws);
        log.info("Skill removed — workerId={} skillId={}", workerId, skillId);
    }
}