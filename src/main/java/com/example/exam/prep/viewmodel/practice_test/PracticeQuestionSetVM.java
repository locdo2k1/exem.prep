package com.example.exam.prep.viewmodel.practice_test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * View model for practice test question sets
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticeQuestionSetVM {
    private UUID id;
    private String title;
    private String description;
    private String imageUrl;
    private List<PracticeQuestionVM> questions;
    private int order;
    private int totalQuestions;
    private int totalScore;

    /**
     * Gets the description of the question set
     * @return the description, or empty string if null
     */
    public String getDescription() {
        return description != null ? description : "";
    }

    /**
     * Gets the image URL of the question set
     * @return the image URL, or null if not set
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Gets the list of questions in the set
     * @return the list of questions, or empty list if null
     */
    public List<PracticeQuestionVM> getQuestions() {
        return questions != null ? questions : Collections.emptyList();
    }

    /**
     * Gets the total number of questions in the set
     * @return the total number of questions
     */
    public int getTotalQuestions() {
        return totalQuestions > 0 ? totalQuestions : (questions != null ? questions.size() : 0);
    }

    /**
     * Gets the total score of all questions in the set
     * @return the total score
     */
    public int getTotalScore() {
        if (totalScore > 0) {
            return totalScore;
        }
        if (questions != null) {
            return questions.stream()
                    .mapToInt(PracticeQuestionVM::getScore)
                    .sum();
        }
        return 0;
    }
}
