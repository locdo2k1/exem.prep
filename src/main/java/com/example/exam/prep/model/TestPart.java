package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "test_parts")
public class TestPart extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @ManyToOne
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @ManyToMany
    @JoinTable(
        name = "test_part_questions",
        joinColumns = @JoinColumn(name = "test_part_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private Set<Question> questions;

    @ManyToMany
    @JoinTable(
        name = "test_part_question_sets",
        joinColumns = @JoinColumn(name = "test_part_id"),
        inverseJoinColumns = @JoinColumn(name = "question_set_id")
    )
    private Set<QuestionSet> questionSets;

    // Constructors
    public TestPart() {}

    public TestPart(Test test, Part part, Integer orderIndex) {
        this.test = test;
        this.part = part;
        this.orderIndex = orderIndex;
    }
}
