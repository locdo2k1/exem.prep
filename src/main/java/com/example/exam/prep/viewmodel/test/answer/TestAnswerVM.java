package com.example.exam.prep.viewmodel.test.answer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * View model for test answers with both hierarchical and flattened question views
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestAnswerVM {
   private UUID testId;
   private String testName;
   private List<FlattenedQuestionVM> flattenedQuestions;
}
