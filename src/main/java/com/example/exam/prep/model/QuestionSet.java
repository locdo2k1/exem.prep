package com.example.exam.prep.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @OneToMany(mappedBy = "questionSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionSetItem> questionSetItems = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "questionSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionSetFile> files = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "questionSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TestQuestionSetDetail> testQuestionSetDetails = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "questionSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TestPartQuestionSet> testPartQuestionSets = new HashSet<>();

    // Constructors
    public QuestionSet() {}

    public QuestionSet(String title, String description, String imageUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
