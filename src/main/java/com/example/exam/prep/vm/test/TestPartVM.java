package com.example.exam.prep.vm.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * View model for Test Part responses.
 * Represents a part of a test containing question sets and individual questions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestPartVM {
    private UUID id;
    private String title;
    private String description;
    private Integer order;
    private List<TestQuestionSetVM> questionSets;
    private List<TestQuestionVM> questions;
    
    // Helper methods if needed
    public int getTotalQuestions() {
        int total = questions != null ? questions.size() : 0;
        if (questionSets != null) {
            total += questionSets.stream()
                    .mapToInt(TestQuestionSetVM::getTotalQuestions)
                    .sum();
        }
        return total;
    }
    
    public int getTotalScore() {
        int total = 0;
        if (questions != null) {
            total += questions.stream().mapToInt(TestQuestionVM::getScore).sum();
        }
        if (questionSets != null) {
            total += questionSets.stream().mapToInt(TestQuestionSetVM::getTotalScore).sum();
        }
        return total;
    }
}
