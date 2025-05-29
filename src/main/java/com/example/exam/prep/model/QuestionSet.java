package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "question_sets")
public class QuestionSet extends BaseEntity {
    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "questionSet")
    private Set<Question> questions;

    @OneToMany(mappedBy = "questionSet")
    private Set<QuestionOption> options;

    @ManyToMany(mappedBy = "questionSets")
    private Set<Test> tests;

    @ManyToMany(mappedBy = "questionSets")
    private Set<TestPart> testParts;

    // Constructors
    public QuestionSet() {}

    public QuestionSet(String title, String description, String imageUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
