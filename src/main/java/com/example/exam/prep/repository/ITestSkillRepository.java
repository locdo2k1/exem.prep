package com.example.exam.prep.repository;

import com.example.exam.prep.model.TestSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ITestSkillRepository extends JpaRepository<TestSkill, UUID> {
    // Custom query methods can be added here if needed
}
