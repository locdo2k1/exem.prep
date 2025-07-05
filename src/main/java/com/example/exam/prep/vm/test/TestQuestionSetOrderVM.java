package com.example.exam.prep.vm.test;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class TestQuestionSetOrderVM {
    private UUID questionSetId;
    private Integer order;
}
