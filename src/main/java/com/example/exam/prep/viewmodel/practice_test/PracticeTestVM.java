package com.example.exam.prep.viewmodel.practice_test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticeTestVM {
    private UUID testId;
    private String testName;
    private List<PracticePartVM> parts;
    private List<PracticeQuestionAndQuestionSetVM> questionAndQuestionSet;
    private List<PracticeFileInfoVM> audioFiles;
}
