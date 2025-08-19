package com.example.exam.prep.repository;

import com.example.exam.prep.model.QuestionResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IQuestionResponseRepository extends GenericRepository<QuestionResponse> {
    
    /**
     * Finds all question responses for a specific test attempt.
     *
     * @param testAttemptId The ID of the test attempt
     * @return List of question responses
     */
    @Query("SELECT DISTINCT qr FROM QuestionResponse qr LEFT JOIN FETCH qr.selectedOptions WHERE qr.testAttempt.id = :testAttemptId")
    List<QuestionResponse> findByTestAttemptId(@Param("testAttemptId") UUID testAttemptId);
}
