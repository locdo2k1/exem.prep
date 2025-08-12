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
    
    @Query("SELECT tqd FROM TestQuestionDetail tqd JOIN FETCH tqd.question WHERE tqd.test.id = :testId ORDER BY tqd.order")
    List<TestQuestionDetail> findByTestId(@Param("testId") UUID testId);
}
