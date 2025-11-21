package com.example.exam.prep.constant.response;

public class SkillResponseMessage {
   // Success messages
   public static final String SKILL_RETRIEVED = "Skill retrieved successfully";
   public static final String SKILLS_RETRIEVED = "Skills retrieved successfully";
   public static final String SKILL_CREATED = "Skill created successfully";
   public static final String SKILL_UPDATED = "Skill updated successfully";
   public static final String SKILL_DELETED = "Skill deleted successfully";

   // Error messages
   public static final String SKILL_NOT_FOUND = "Skill not found with id: %s";
   public static final String SKILL_NOT_FOUND_BY_CODE = "Skill not found with code: %s";
   public static final String SKILL_CODE_EXISTS = "Skill with code %s already exists";
   public static final String SKILL_NAME_EXISTS = "Skill with name %s already exists";
}
