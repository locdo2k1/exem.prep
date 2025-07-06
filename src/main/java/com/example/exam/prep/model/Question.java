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
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionSetItem> questionSetItems = new HashSet<>();

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
    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TestQuestionDetail> testQuestionDetails = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TestPartQuestion> testPartQuestions = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FileInfo> fileInfos = new HashSet<>();

    // Constructors
    public Question() {}

    public Question(QuestionType questionType, String prompt) {
        this.questionType = questionType;
        this.prompt = prompt;
    }

    public Question(QuestionType questionType, String prompt, QuestionCategory category) {
        this.questionType = questionType;
        this.prompt = prompt;
        this.category = category;
    }
}
