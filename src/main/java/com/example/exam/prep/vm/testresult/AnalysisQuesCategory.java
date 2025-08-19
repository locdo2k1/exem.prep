package com.example.exam.prep.vm.testresult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Optional;
import java.util.UUID;

/**
 * View Model for representing question category analysis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisQuesCategory {
    /**
     * The unique identifier of the part this category belongs to.
     * This is an Optional field as the category might not be associated with any specific part.
     * This field is ignored during JSON serialization.
     */
    @JsonIgnore
    private Optional<UUID> partId;

    /**
     * Name of the question category
     */
    private String categoryName;
    
    /**
     * Number of correct answers in this category
     */
    private Integer correctNumber;
    
    /**
     * Number of incorrect answers in this category
     */
    private Integer incorrectNumber;
    
    /**
     * Number of skipped questions in this category
     */
    private Integer skipNumber;
    
    /**
     * Accuracy percentage for this category
     */
    private Double accuracy;
    
    /**
     * List of question results in this category, ordered by the question's order in the test
     */
    private List<QuestionResultVM> questions;
    
    /**
     * Gets the list of questions, ordered by their position in the test
     * @return ordered list of questions
     */
    public List<QuestionResultVM> getQuestions() {
        if (questions != null) {
            questions.sort((q1, q2) -> Integer.compare(q1.getOrder(), q2.getOrder()));
        }
        return questions;
    }
    
    /**
     * Sets the list of questions
     * @param questions list of questions to set
     */
    public void setQuestions(List<QuestionResultVM> questions) {
        if (questions != null) {
            questions.sort((q1, q2) -> Integer.compare(q1.getOrder(), q2.getOrder()));
        }
        this.questions = questions;
    }
}
