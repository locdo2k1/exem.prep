package com.example.exam.prep.service.impl;

import com.example.exam.prep.constant.response.SkillResponseMessage;
import com.example.exam.prep.exception.ResourceNotFoundException;
import com.example.exam.prep.model.Skill;
import com.example.exam.prep.repository.ISkillRepository;
import com.example.exam.prep.service.ISkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements ISkillService {

   private final ISkillRepository skillRepository;

   @Override
   public List<Skill> getAllSkills() {
      return skillRepository.findAll();
   }

   @Override
   public Skill getSkillById(UUID id) {
      return skillRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(String.format(SkillResponseMessage.SKILL_NOT_FOUND, id)));
   }

   @Override
   public Skill getSkillByCode(String code) {
      return skillRepository.findAll().stream()
            .filter(skill -> skill.getCode().equals(code))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(
                  String.format(SkillResponseMessage.SKILL_NOT_FOUND_BY_CODE, code)));
   }

   @Override
   @Transactional
   public Skill createSkill(Skill skill) {
      if (existsByCode(skill.getCode())) {
         throw new IllegalArgumentException(String.format(SkillResponseMessage.SKILL_CODE_EXISTS, skill.getCode()));
      }
      if (existsByName(skill.getName())) {
         throw new IllegalArgumentException(String.format(SkillResponseMessage.SKILL_NAME_EXISTS, skill.getName()));
      }
      return skillRepository.save(skill);
   }

   @Override
   @Transactional
   public Skill updateSkill(UUID id, Skill skillDetails) {
      Skill skill = getSkillById(id);

      // Check if code is being changed and if the new code already exists
      if (!skill.getCode().equals(skillDetails.getCode()) && existsByCode(skillDetails.getCode())) {
         throw new IllegalArgumentException(
               String.format(SkillResponseMessage.SKILL_CODE_EXISTS, skillDetails.getCode()));
      }

      // Check if name is being changed and if the new name already exists
      if (!skill.getName().equals(skillDetails.getName()) && existsByName(skillDetails.getName())) {
         throw new IllegalArgumentException(
               String.format(SkillResponseMessage.SKILL_NAME_EXISTS, skillDetails.getName()));
      }

      skill.setCode(skillDetails.getCode());
      skill.setName(skillDetails.getName());
      skill.setDescription(skillDetails.getDescription());

      return skillRepository.save(skill);
   }

   @Override
   @Transactional
   public void deleteSkill(UUID id) {
      Skill skill = getSkillById(id);
      skillRepository.delete(skill);
   }

   @Override
   public boolean existsByCode(String code) {
      return skillRepository.findAll().stream()
            .anyMatch(skill -> skill.getCode().equals(code));
   }

   @Override
   public boolean existsByName(String name) {
      return skillRepository.findAll().stream()
            .anyMatch(skill -> skill.getName().equals(name));
   }
}
