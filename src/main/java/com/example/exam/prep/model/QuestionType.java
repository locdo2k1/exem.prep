package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "question_types")
public class QuestionType extends BaseEntity {
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    // Constructors
    public QuestionType() {}

    public QuestionType(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
