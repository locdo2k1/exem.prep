package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "test_part_details")
public class TestPartDetail extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "test_part_id", nullable = false)
    private TestPart testPart;

    @ManyToOne
    @JoinColumn(name = "question_set_id", nullable = false)
    private QuestionSet questionSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private Question question;

    @Column(name = "`order`", nullable = false)
    private Integer order;
}
