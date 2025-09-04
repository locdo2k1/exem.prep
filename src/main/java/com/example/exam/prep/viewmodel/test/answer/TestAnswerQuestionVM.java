package com.example.exam.prep.viewmodel.test.answer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View model for test answer questions
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestAnswerQuestionVM {
   private TestAnswerOptionVM correctOption;
   private String transcript;
   private int order;
}
