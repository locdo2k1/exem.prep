package com.example.exam.prep.vm.test;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TestPartVM {
    private UUID partId;
    private List<TestQuestionSetOrderVM> listQuestionSet;
    private List<TestQuestionOrderVM> listQuestion;
    private Integer order;
}
