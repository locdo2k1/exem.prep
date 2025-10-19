package com.example.exam.prep.model.viewmodels;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
public class TestAttemptWithNameVM extends TestAttemptInfoVM {
    private String testName;
}
