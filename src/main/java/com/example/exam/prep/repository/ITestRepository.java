package com.example.exam.prep.repository;

import com.example.exam.prep.model.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ITestRepository extends GenericRepository<Test> {
    @EntityGraph(attributePaths = {
        "testParts",
        "testFiles",
        "testQuestionDetails",
        "testQuestionSetDetails",
        "testSkills"
    })
    @Query("SELECT t FROM Test t WHERE t.id = :id")
    Optional<Test> findByIdWithRelations(@Param("id") UUID id);

    @Query("SELECT DISTINCT t FROM Test t " +
           "LEFT JOIN t.testCategory tc " +
           "LEFT JOIN t.testSkills ts " +
           "LEFT JOIN ts.skill s " +
           "WHERE (:search IS NULL OR " +
           "LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(tc.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Test> searchTests(@Param("search") String search, Pageable pageable);

    @Query("SELECT t FROM Test t WHERE t.testCategory.id = :testCategoryId")
    Page<Test> findByTestCategoryId(@Param("testCategoryId") UUID testCategoryId, Pageable pageable);

    @Query("SELECT t FROM Test t WHERE " +
           "(:testCategoryId IS NULL OR t.testCategory.id = :testCategoryId) AND " +
           "(:keyword IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Test> findByTestCategoryIdAndKeyword(
            @Param("testCategoryId") UUID testCategoryId,
            @Param("keyword") String keyword,
            Pageable pageable);
}
