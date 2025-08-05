package com.example.exam.prep.viewmodel.practice_test;

import com.example.exam.prep.viewmodel.TestPartAttemptVM;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticeTestResultVM {
    private UUID testAttemptId;
    private UUID testId;
    private UUID userId;
    private Double overallScore;
    private List<TestPartAttemptVM> partResults;
}
