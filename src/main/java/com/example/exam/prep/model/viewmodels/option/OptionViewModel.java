package com.example.exam.prep.model.viewmodels.option;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OptionViewModel {
    private UUID id;
    private String text;
    private boolean isCorrect;
    private Integer displayOrder;
}
