package com.example.exam.prep.vm.test;

import com.example.exam.prep.model.viewmodels.question.QuestionCategoryViewModel;
import com.example.exam.prep.model.viewmodels.question.QuestionTypeViewModel;
import com.example.exam.prep.model.viewmodels.file.FileInfoViewModel;
import com.example.exam.prep.model.viewmodels.option.OptionViewModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * View model for TestQuestion responses.
 * Aligns with the structure of QuestionViewModel.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestQuestionVM {
    private UUID id;
    private UUID partId; // Reference to the containing part
    private String prompt;
    private QuestionCategoryViewModel questionCategory;
    private QuestionTypeViewModel questionType;
    private int score;
    private List<String> questionAnswers;
    private List<OptionViewModel> options;
    private List<FileInfoViewModel> questionAudios;
    private Integer order;
}
