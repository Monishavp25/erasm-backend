package com.erasm.service;

import com.erasm.dto.SkillDTO;
import com.erasm.entity.Skill;
import com.erasm.exception.SkillNotFoundException;
import com.erasm.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public SkillDTO addSkill(SkillDTO dto) {
        Skill skill = Skill.builder().name(dto.getName()).build();
        skill = skillRepository.save(skill);
        return toDto(skill);
    }

    public SkillDTO updateSkill(Long id, SkillDTO dto) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new SkillNotFoundException("Skill not found: " + id));
        skill.setName(dto.getName());
        return toDto(skillRepository.save(skill));
    }

    public void deleteSkill(Long id) {
        if (!skillRepository.existsById(id)) {
            throw new SkillNotFoundException("Skill not found: " + id);
        }
        skillRepository.deleteById(id);
    }

    public List<SkillDTO> listSkills() {
        return skillRepository.findAll().stream().map(this::toDto).toList();
    }

    private SkillDTO toDto(Skill skill) {
        return SkillDTO.builder().id(skill.getId()).name(skill.getName()).build();
    }
}
