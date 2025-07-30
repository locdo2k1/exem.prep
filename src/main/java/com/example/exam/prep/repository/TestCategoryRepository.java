package com.example.exam.prep.repository;

import com.example.exam.prep.model.TestCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestCategoryRepository extends JpaRepository<TestCategory, Long> {
    Optional<TestCategory> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByName(String name);
}
