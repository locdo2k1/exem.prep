package com.example.exam.prep.vm.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestSkillVM {
    private java.util.UUID id;
    private String code;
    private String name;
    private String description;
}
