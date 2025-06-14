package com.example.exam.prep.model.viewmodels.question;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@lombok.Data
public class UpdateQuestionViewModel {
    private String prompt;
    private UUID questionTypeId;
    private UUID categoryId;
    private String options;
    private String blankAnswers;
    private List<MultipartFile> audios = new ArrayList<>();
    private String deletedAudiosIds;
    private int score;
}
