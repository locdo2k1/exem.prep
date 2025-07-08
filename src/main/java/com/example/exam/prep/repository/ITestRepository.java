package com.example.exam.prep.repository;

import com.example.exam.prep.model.Test;
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
}
