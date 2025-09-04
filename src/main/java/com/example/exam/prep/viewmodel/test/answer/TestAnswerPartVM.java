package com.example.exam.prep.viewmodel.test.answer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestAnswerPartVM {
   private UUID id;
   private String name;
   private List<TestAnswerQuestionAndQuestionSetVM> questionsAndQuestionSets;
}
