package com.example.exam.prep.repository;

import com.example.exam.prep.model.TestCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ITestCategoryRepository extends JpaRepository<TestCategory, UUID> {
    // Add custom query methods here if needed
}
