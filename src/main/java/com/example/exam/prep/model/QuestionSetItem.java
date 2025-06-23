package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "question_set_items")
public class QuestionSetItem extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_set_id", nullable = false)
    private QuestionSet questionSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "display_order")
    private Integer order;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "custom_score")
    private Integer customScore;

    // Constructors
    public QuestionSetItem() {}

    public QuestionSetItem(QuestionSet questionSet, Question question) {
        this.questionSet = questionSet;
        this.question = question;
    }

    public QuestionSetItem(QuestionSet questionSet, Question question, Integer order, Integer customScore) {
        this.questionSet = questionSet;
        this.question = question;
        this.order = order;
        this.customScore = customScore;
    }
}
