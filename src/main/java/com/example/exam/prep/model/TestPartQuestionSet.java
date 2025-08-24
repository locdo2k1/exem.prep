package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "test_part_question_sets")
public class TestPartQuestionSet extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "test_part_id", nullable = false)
    private TestPart testPart;

    @ManyToOne
    @JoinColumn(name = "question_set_id", nullable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private QuestionSet questionSet;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    public TestPartQuestionSet() {}

    public TestPartQuestionSet(TestPart testPart, QuestionSet questionSet, Integer displayOrder) {
        this.testPart = testPart;
        this.questionSet = questionSet;
        this.displayOrder = displayOrder;
    }
}
