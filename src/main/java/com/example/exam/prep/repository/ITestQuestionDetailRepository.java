package com.example.exam.prep.repository;

import com.example.exam.prep.model.TestQuestionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ITestQuestionDetailRepository extends JpaRepository<TestQuestionDetail, UUID> {
    
    @Query("SELECT tqd FROM TestQuestionDetail tqd JOIN FETCH tqd.question q WHERE tqd.test.id = :testId AND q.isDeleted = false ORDER BY tqd.order")
    List<TestQuestionDetail> findByTestId(@Param("testId") UUID testId);
    
    @Query("SELECT COUNT(tqd) FROM TestQuestionDetail tqd JOIN tqd.question q WHERE tqd.test.id = :testId AND q.isDeleted = false")
    int countByTestId(@Param("testId") UUID testId);
    
    @Query("SELECT COUNT(q2.id) FROM TestQuestionSetDetail tqsd " +
           "JOIN tqsd.questionSet qs " +
           "JOIN qs.questionSetItems qsi " +
           "JOIN qsi.question q2 " +
           "WHERE tqsd.test.id = :testId AND q2.isDeleted = false")
    int countQuestionsInQuestionSetsByTestId(@Param("testId") UUID testId);
    
    @Query("SELECT COUNT(q1.id) + COALESCE((SELECT COUNT(q2.id) " +
           "FROM TestPartQuestionSet tpqs " +
           "JOIN tpqs.questionSet qs " +
           "JOIN qs.questionSetItems qsi " +
           "JOIN qsi.question q2 " +
           "WHERE tpqs.testPart.test.id = :testId AND q2.isDeleted = false), 0) " +
           "FROM TestPartQuestion tpq " +
           "JOIN tpq.question q1 " +
           "JOIN tpq.testPart tp " +
           "WHERE tp.test.id = :testId AND q1.isDeleted = false")
    int countQuestionsInTestPartsByTestId(@Param("testId") UUID testId);
}
