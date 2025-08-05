package com.example.exam.prep.viewmodel.practice_test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticeQuestionVM {
    private UUID id;
    private String text;
    private String type;
    private List<PracticeOptionVM> options;
    private int order;
}
