package com.example.exam.prep.viewmodel.practice_test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticeQuestionSetVM {
    private UUID id;
    private String title;
    private List<PracticeQuestionVM> questions;
    private int order;
}
