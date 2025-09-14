package com.example.exam.prep.viewmodel.test.answer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View model for flattened question format
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlattenedQuestionVM {
    private int order;
    private String part;
    private String answer;
    private String transcript;
    private boolean showTranscript;
    private boolean isCorrect;
    private String userAnswer;
}
