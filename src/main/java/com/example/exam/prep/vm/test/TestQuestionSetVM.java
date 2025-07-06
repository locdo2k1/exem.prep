package com.example.exam.prep.vm.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * View model for TestQuestionSet responses.
 * Aligns with the structure of QuestionSetVM.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestQuestionSetVM {
    private UUID id;
    private UUID partId; // Reference to the containing part
    private String title;
    private String description;
    private String imageUrl;
    private Integer order;
    private List<TestQuestionVM> questions;
    private int totalQuestions;
    private int totalScore;
}

