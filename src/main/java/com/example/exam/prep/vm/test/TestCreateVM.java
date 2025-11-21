package com.example.exam.prep.vm.test;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TestCreateVM {
    private String title;
    private List<TestPartVM> listPart;
    private List<TestQuestionSetOrderVM> listQuestionSet;
    private List<TestQuestionOrderVM> listQuestion;
    private List<UUID> skillIds;
    private UUID testCategoryId;
    private Integer durationMinutes;
}
