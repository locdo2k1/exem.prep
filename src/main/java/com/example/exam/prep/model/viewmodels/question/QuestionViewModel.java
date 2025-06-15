package com.example.exam.prep.model.viewmodels.question;

import com.example.exam.prep.model.viewmodels.file.FileInfoViewModel;
import com.example.exam.prep.model.viewmodels.option.OptionViewModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionViewModel {
    private UUID id;
    private String prompt;
    private QuestionCategoryViewModel questionCategory;
    private QuestionTypeViewModel questionType;
    private int score;
    private List<String> questionAnswers;
    private List<OptionViewModel> options;
    private List<FileInfoViewModel> questionAudios;
}
