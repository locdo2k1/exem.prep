package com.example.exam.prep.vm.testresult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResultOverallVM {
    private int totalQuestions;
    private int correctAnswers;
    private int incorrectAnswers;
    private int skippedQuestions;
    private double accuracyPercentage;
    private double score;
    private String completionTime; // format: HH:mm:ss

    // Computed field for attempted questions
    public int getAttemptedQuestions() {
        return correctAnswers + incorrectAnswers;
    }
}
