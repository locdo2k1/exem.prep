package com.example.exam.prep.model.viewmodels.question;

import lombok.Data;
import java.util.UUID;

@Data
public class QuestionTypeViewModel {
    private UUID id;
    private String name;
    private String description;
}
