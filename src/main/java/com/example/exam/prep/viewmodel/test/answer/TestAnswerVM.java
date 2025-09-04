package com.example.exam.prep.viewmodel.test.answer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestAnswerVM {
   private UUID testId;
   private String testName;
   private List<TestAnswerPartVM> parts;
   private List<TestAnswerQuestionAndQuestionSetVM> questionAndQuestionSet;
}
