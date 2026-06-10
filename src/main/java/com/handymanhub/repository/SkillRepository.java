package com.handymanhub.repository;
import com.handymanhub.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill,Long> {
          Optional<Skill> findByNameIgnoreCase(String name);
          List<Skill> findByCategoryIgnoreCase(String category);
          boolean existsByNameIgnoreCase(String name);
          @Query("SELECT s FROM Skill s ORDER BY s.category,s.name")
          List<Skill> findAllOrderedByCategoryAndName();


          }
