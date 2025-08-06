package com.example.exam.prep.viewmodel.practice_test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * View model for practice test questions
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticeQuestionVM {
    private UUID id;
    private String text;
    private String questionType;
    private Integer score;
    private int order;
    private List<PracticeOptionVM> options;
    private List<PracticeFileInfoVM> questionAudios; // For audio files

    // Alias for text to maintain backward compatibility
    public String getPrompt() {
        return text;
    }

    public void setPrompt(String prompt) {
        this.text = prompt;
    }

    // Getter for score that handles null case
    public int getScore() {
        return score != null ? score : 0;
    }

    /**
     * Sets the question type
     * @param type The question type as string
     */
    public void setType(String type) {
        this.questionType = type;
    }

    /**
     * Gets the question type
     * @return The question type as string
     */
    public String getType() {
        return questionType;
    }
}
