package com.example.exam.prep.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Represents a single question answer in a practice test submission
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnswerRequest {
    private UUID questionId;
    private List<UUID> selectedOptionIds;
    private String answerText;
}
