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
@Table(name = "questions")
public class Question extends BaseEntity {
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_set_id", nullable = true)
    private QuestionSet questionSet;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_type_id", nullable = false)
    private QuestionType questionType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private QuestionCategory category;

    @Column(name = "clip_number")
    private Integer clipNumber;

    @Column(name = "display_order")
    private Integer order;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "prompt", columnDefinition = "NVARCHAR(MAX)")
    private String prompt;

    @Column(name = "score")
    private Integer score;

    @JsonIgnore
    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    private Set<Option> options;

    @JsonIgnore
    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    private Set<FillBlankAnswer> fillBlankAnswers;

    @JsonIgnore
    @ManyToMany(mappedBy = "questions", fetch = FetchType.LAZY)
    private Set<Test> tests = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "questions", fetch = FetchType.LAZY)
    private Set<TestPart> testParts = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FileInfo> fileInfos = new HashSet<>();

    // Constructors
    public Question() {}

    public Question(QuestionSet questionSet, QuestionType questionType, String prompt) {
        this.questionSet = questionSet;
        this.questionType = questionType;
        this.prompt = prompt;
    }

    public Question(QuestionSet questionSet, QuestionType questionType, String prompt, QuestionCategory category) {
        this.questionSet = questionSet;
        this.questionType = questionType;
        this.prompt = prompt;
        this.category = category;
    }
}
