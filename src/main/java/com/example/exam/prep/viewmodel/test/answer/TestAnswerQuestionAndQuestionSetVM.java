package com.example.exam.prep.viewmodel.test.answer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestAnswerQuestionAndQuestionSetVM {
   private UUID id;
   private int order;
   private TestAnswerQuestionSetVM questionSet;
   private TestAnswerQuestionVM question;
}
