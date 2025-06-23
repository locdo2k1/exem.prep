package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "question_sets")
public class QuestionSet extends BaseEntity {
    @Column(name = "title")
    private String title;

    @Column(name = "display_order")
    private Integer order;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "questionSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionSetItem> questionSetItems = new HashSet<>();

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
