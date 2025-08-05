package com.example.exam.prep.viewmodel.practice_test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticeOptionVM {
    private UUID id;
    private String text;
}
