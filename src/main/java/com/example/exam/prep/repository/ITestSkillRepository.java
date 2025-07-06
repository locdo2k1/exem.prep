package com.example.exam.prep.repository;

import com.example.exam.prep.model.TestSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ITestSkillRepository extends JpaRepository<TestSkill, UUID> {
    List<TestSkill> findByTestId(UUID testId);
}
