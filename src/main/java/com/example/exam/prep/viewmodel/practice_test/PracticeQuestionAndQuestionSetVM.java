package com.example.exam.prep.viewmodel.practice_test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticeQuestionAndQuestionSetVM {
    private UUID id;
    private int order;
    private PracticeQuestionSetVM questionSet;
    private PracticeQuestionVM question;
}
