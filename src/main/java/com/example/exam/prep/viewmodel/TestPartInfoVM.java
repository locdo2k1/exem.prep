package com.example.exam.prep.viewmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestPartInfoVM {
    private UUID id;
    private String title;
    private int order;
    private int questionCount;
    private List<String> questionCategories;
}
