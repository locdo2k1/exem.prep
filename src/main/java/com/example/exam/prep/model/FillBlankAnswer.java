package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "fill_blank_answers")
public class FillBlankAnswer extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private Question question;

    @Column(name = "answer_text", nullable = false)
    private String answerText;

    // Constructors
    public FillBlankAnswer() {}

    public FillBlankAnswer(Question question, String answerText) {
        this.question = question;
        this.answerText = answerText;
    }
}
