package com.handymanhub.service;

import com.handymanhub.exception.ResourceNotFoundException;
import com.handymanhub.model.Skill;
import com.handymanhub.repository.SkillRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SkillService {

    private static final Logger log = LoggerFactory.getLogger(SkillService.class);

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public List<Skill> getAll() {
        log.debug("Fetching all skills");
        return skillRepository.findAllOrderedByCategoryAndName();
    }

    public Skill getById(Long id) {
        log.debug("Fetching skill id={}", id);
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", id));
    }

    public List<Skill> getByCategory(String category) {
        log.debug("Fetching skills in category={}", category);
        return skillRepository.findByCategoryIgnoreCase(category);
    }

    @Transactional
    public Skill create(String name, String category, String description) {
        log.info("Creating skill name={}", name);

        if (skillRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Skill already exists: " + name);
        }

        Skill skill = Skill.builder()
                .name(name)
                .category(category)
                .description(description)
                .build();

        Skill saved = skillRepository.save(skill);
        log.info("Skill created id={} name={}", saved.getId(), saved.getName());
        return saved;
    }

    @Transactional
    public Skill update(Long id, String name, String category, String description) {
        log.info("Updating skill id={}", id);

        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", id));

        skill.setName(name);
        skill.setCategory(category);
        skill.setDescription(description);

        return skillRepository.save(skill);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting skill id={}", id);

        if (!skillRepository.existsById(id)) {
            throw new ResourceNotFoundException("Skill", id);
        }

        skillRepository.deleteById(id);
        log.info("Skill id={} deleted", id);
    }
}