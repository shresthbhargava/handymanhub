package com.handymanhub.repository;

import com.handymanhub.model.WorkerSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkerSkillRepository extends JpaRepository<WorkerSkill, Long> {

    List<WorkerSkill> findByWorkerId(Long workerId);

    Optional<WorkerSkill> findByWorkerIdAndSkillId(Long workerId, Long skillId);

    boolean existsByWorkerIdAndSkillId(Long workerId, Long skillId);

    @Query("SELECT ws FROM WorkerSkill ws " +
            "WHERE ws.skill.id = :skillId " +
            "AND ws.worker.available = true " +
            "AND ws.worker.pincode = :pincode")
    List<WorkerSkill> findAvailableWorkersBySkillAndPincode(
            @Param("skillId") Long skillId,
            @Param("pincode") String pincode);
}