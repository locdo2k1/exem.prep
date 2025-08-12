package com.example.exam.prep.vm.testresult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * View Model for representing a question in a test result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResultVM {
    /**
     * Order of the question in the test
     */
    private int order;
    
    /**
     * Context or instructions for the question
     */
    private String context;
    
    /**
     * Explanation for the correct answer
     */
    private String explanation;
    
    /**
     * Transcript for audio/video questions
     */
    private String transcript;
    
    /**
     * Indicates if the user's answer is correct
     */
    private Boolean isCorrect;
    
    /**
     * List of correct options for multiple-choice questions
     */
    private List<OptionResultVM> correctOptions;
    
    /**
     * List of correct answers for text-based questions
     */
    private List<String> correctAnswers;
    
    /**
     * User's answer for text-based questions
     */
    private String userAnswer;
    
    /**
     * All available options for the question
     */
    private List<OptionResultVM> options;
}
