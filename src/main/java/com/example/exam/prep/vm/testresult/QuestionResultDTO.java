package com.example.exam.prep.vm.testresult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResultDTO {
    /**
     * ID of the part this question belongs to
     */
    private Optional<UUID> PartId;

    /**
     * Type of the question (e.g., MULTIPLE_CHOICE, FILL_IN_THE_BLANK)
     */
    private String questionType;

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

    /**
     * List of categories this question belongs to
     */
    private List<String> questionCategories;
}