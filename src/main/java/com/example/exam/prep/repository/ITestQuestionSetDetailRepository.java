package com.example.exam.prep.repository;

import com.example.exam.prep.model.TestQuestionSetDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ITestQuestionSetDetailRepository extends JpaRepository<TestQuestionSetDetail, UUID> {
    
    @Query("SELECT tqsd FROM TestQuestionSetDetail tqsd " +
           "JOIN FETCH tqsd.questionSet qs " +
           "LEFT JOIN FETCH qs.questionSetItems qsi " +
           "LEFT JOIN FETCH qsi.question " +
           "WHERE tqsd.test.id = :testId ORDER BY tqsd.order")
    List<TestQuestionSetDetail> findByTestId(@Param("testId") UUID testId);
}
