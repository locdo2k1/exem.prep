package com.example.exam.prep.model.viewmodels.question.category;

import lombok.Data;
import java.util.UUID;

@Data
public class QuestionCategoryVM {
    private UUID id;
    private String code;
    private String skill;
    private String name;
}
