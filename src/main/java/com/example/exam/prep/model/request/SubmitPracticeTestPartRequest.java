package com.example.exam.prep.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

/**
 * Request object for submitting a practice test part
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitPracticeTestPartRequest {
    private UUID testId;
    private UUID userId;
    
    /**
     * Duration taken to complete the test in seconds
     */
    private int duration;
    
    private List<QuestionAnswerRequest> questionAnswers;
    private List<UUID> listPartId;
}
