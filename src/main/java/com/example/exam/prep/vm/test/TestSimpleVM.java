package com.example.exam.prep.vm.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestSimpleVM {
    private UUID id;
    private String title;
    private String description;
    private Integer durationInMinutes;
    private Integer totalMarks;
    private Boolean isPublished;
    private String subject;
    private String grade;
}
