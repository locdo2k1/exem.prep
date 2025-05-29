package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "question_categories")
public class QuestionCategory extends BaseEntity {
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "skill", nullable = false)
    private String skill;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "category")
    private Set<Question> questions;

    // Constructors
    public QuestionCategory() {}

    public QuestionCategory(String code, String skill, String name) {
        this.code = code;
        this.skill = skill;
        this.name = name;
    }
}
