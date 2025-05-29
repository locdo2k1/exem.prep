package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "questions")
public class Question extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "question_set_id", nullable = false)
    private QuestionSet questionSet;

    @ManyToOne
    @JoinColumn(name = "question_type_id", nullable = false)
    private QuestionType questionType;

    @Column(name = "clip_number")
    private Integer clipNumber;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "prompt", columnDefinition = "NVARCHAR(MAX)")
    private String prompt;

    @ManyToMany
    @JoinTable(
        name = "question_answers",
        joinColumns = @JoinColumn(name = "question_id"),
        inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private Set<QuestionOption> correctOptions;

    @OneToMany(mappedBy = "question")
    private Set<FillBlankAnswer> fillBlankAnswers;

    // Constructors
    public Question() {}

    public Question(QuestionSet questionSet, QuestionType questionType, String prompt) {
        this.questionSet = questionSet;
        this.questionType = questionType;
        this.prompt = prompt;
    }
}
