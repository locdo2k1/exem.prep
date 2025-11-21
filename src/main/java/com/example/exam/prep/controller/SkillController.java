package com.example.exam.prep.controller;

import com.example.exam.prep.constant.response.SkillResponseMessage;
import com.example.exam.prep.model.Skill;
import com.example.exam.prep.service.ISkillService;
import com.example.exam.prep.model.viewmodels.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SkillController {

   private final ISkillService skillService;

   @GetMapping
   public ResponseEntity<ApiResponse<List<Skill>>> getAllSkills() {
      try {
         List<Skill> skills = skillService.getAllSkills();
         return ResponseEntity.ok(ApiResponse.success(skills, SkillResponseMessage.SKILLS_RETRIEVED));
      } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body(ApiResponse.error("Error retrieving skills: " + e.getMessage(),
                     HttpStatus.INTERNAL_SERVER_ERROR.value()));
      }
   }

   @GetMapping("/{id}")
   public ResponseEntity<ApiResponse<Skill>> getSkillById(@PathVariable UUID id) {
      try {
         Skill skill = skillService.getSkillById(id);
         return ResponseEntity.ok(ApiResponse.success(skill, SkillResponseMessage.SKILL_RETRIEVED));
      } catch (com.example.exam.prep.exception.ResourceNotFoundException e) {
         return ResponseEntity
               .status(HttpStatus.NOT_FOUND)
               .body(ApiResponse.error(e.getMessage(), HttpStatus.NOT_FOUND.value()));
      }
   }

   @GetMapping("/code/{code}")
   public ResponseEntity<ApiResponse<Skill>> getSkillByCode(@PathVariable String code) {
      try {
         Skill skill = skillService.getSkillByCode(code);
         return ResponseEntity.ok(ApiResponse.success(skill, SkillResponseMessage.SKILL_RETRIEVED));
      } catch (com.example.exam.prep.exception.ResourceNotFoundException e) {
         return ResponseEntity
               .status(HttpStatus.NOT_FOUND)
               .body(ApiResponse.error(e.getMessage(), HttpStatus.NOT_FOUND.value()));
      }
   }

   @PostMapping
   public ResponseEntity<ApiResponse<Skill>> createSkill(@RequestBody Skill skill) {
      try {
         Skill createdSkill = skillService.createSkill(skill);
         return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(ApiResponse.success(createdSkill, SkillResponseMessage.SKILL_CREATED));
      } catch (IllegalArgumentException e) {
         return ResponseEntity
               .status(HttpStatus.BAD_REQUEST)
               .body(ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
      }
   }

   @PutMapping("/{id}")
   public ResponseEntity<ApiResponse<Skill>> updateSkill(
         @PathVariable UUID id,
         @RequestBody Skill skillDetails) {
      try {
         Skill updatedSkill = skillService.updateSkill(id, skillDetails);
         return ResponseEntity.ok(ApiResponse.success(updatedSkill, SkillResponseMessage.SKILL_UPDATED));
      } catch (com.example.exam.prep.exception.ResourceNotFoundException e) {
         return ResponseEntity
               .status(HttpStatus.NOT_FOUND)
               .body(ApiResponse.error(e.getMessage(), HttpStatus.NOT_FOUND.value()));
      } catch (IllegalArgumentException e) {
         return ResponseEntity
               .status(HttpStatus.BAD_REQUEST)
               .body(ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
      }
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<ApiResponse<Void>> deleteSkill(@PathVariable UUID id) {
      try {
         skillService.deleteSkill(id);
         return ResponseEntity
               .status(HttpStatus.NO_CONTENT)
               .body(ApiResponse.success(null, SkillResponseMessage.SKILL_DELETED));
      } catch (com.example.exam.prep.exception.ResourceNotFoundException e) {
         return ResponseEntity
               .status(HttpStatus.NOT_FOUND)
               .body(ApiResponse.error(e.getMessage(), HttpStatus.NOT_FOUND.value()));
      } catch (Exception e) {
         return ResponseEntity
               .status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body(ApiResponse.error("Error deleting skill: " + e.getMessage(),
                     HttpStatus.INTERNAL_SERVER_ERROR.value()));
      }
   }
}
