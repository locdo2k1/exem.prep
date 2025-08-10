package com.example.exam.prep.repository;

import com.example.exam.prep.model.TestPart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ITestPartRepository extends GenericRepository<TestPart> {
    @EntityGraph(attributePaths = {"part"})
    @Query("SELECT tp FROM TestPart tp WHERE tp.test.id = :testId ORDER BY tp.orderIndex")
    List<TestPart> findByTestIdWithParts(@Param("testId") UUID testId);
    
    @EntityGraph(attributePaths = {"testPartQuestions.question", "testPartQuestionSets.questionSet.questionSetItems.question"})
    @Query("SELECT tp FROM TestPart tp WHERE tp.part.id = :partId AND tp.test.id = :testId")
    Optional<TestPart> findByPartIdAndTestId(
            @Param("partId") UUID partId, 
            @Param("testId") UUID testId
    );
}
