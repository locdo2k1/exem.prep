package com.example.exam.prep.model.viewmodels.question;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CreateQuestionViewModel {
    private String prompt;
    private String transcript;
    private UUID questionTypeId;
    private UUID categoryId;
    private String options;
    private String blankAnswers;
    private List<MultipartFile> audios = new ArrayList<>();
    private int score;
}
