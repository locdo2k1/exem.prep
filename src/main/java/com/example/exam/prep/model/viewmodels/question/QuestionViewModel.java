package com.example.exam.prep.model.viewmodels.question;

import com.example.exam.prep.model.*;
import lombok.Data;
import java.util.List;

@Data
public class QuestionViewModel {
    private String prompt;
    private QuestionCategory questionCategory;
    private QuestionType questionType;
    private int score;
    private List<String> questionAnswers;
    private List<Option> options;
    private List<FileInfo> questionAudios;
}
