package com.example.exam.prep.viewmodel.test.answer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * View model for test answer question sets
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestAnswerQuestionSetVM {
   private List<TestAnswerQuestionVM> questions;
   private int order;

   /**
    * Gets the list of questions in the set
    * 
    * @return the list of questions, or empty list if null
    */
   public List<TestAnswerQuestionVM> getQuestions() {
      return questions != null ? questions : Collections.emptyList();
   }
}
