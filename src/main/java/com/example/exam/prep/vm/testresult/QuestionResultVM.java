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
     * Outer content (description) of the parent question set if this question
     * belongs to one
     */
    private String outerContent;

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

    /**
     * Type of the question (e.g., MULTIPLE_CHOICE, FILL_IN_THE_BLANK)
     */
    private String questionType;

    /**
     * List of categories this question belongs to
     */
    private List<String> questionCategories;

    /**
     * List of audio/video files associated with this question
     */
    private List<FileInfoResultVM> questionAudios;
}
