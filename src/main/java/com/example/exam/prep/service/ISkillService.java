package com.example.exam.prep.service;

import com.example.exam.prep.model.Skill;
import java.util.List;
import java.util.UUID;

public interface ISkillService {
   List<Skill> getAllSkills();

   Skill getSkillById(UUID id);

   Skill getSkillByCode(String code);

   Skill createSkill(Skill skill);

   Skill updateSkill(UUID id, Skill skill);

   void deleteSkill(UUID id);

   boolean existsByCode(String code);

   boolean existsByName(String name);
}
