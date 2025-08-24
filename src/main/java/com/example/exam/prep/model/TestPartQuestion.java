package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "test_part_questions")
public class TestPartQuestion extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "test_part_id", nullable = false)
    private TestPart testPart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private Question question;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    public TestPartQuestion() {}

    public TestPartQuestion(TestPart testPart, Question question, Integer displayOrder) {
        this.testPart = testPart;
        this.question = question;
        this.displayOrder = displayOrder;
    }
}
