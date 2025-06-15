package com.example.exam.prep.model.viewmodels.question;

import lombok.Data;
import java.util.UUID;

@Data
public class QuestionCategoryViewModel {
    private UUID id;
    private String code;
    private String skill;
    private String name;
}
