package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
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

    @OneToMany(mappedBy = "testPart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TestPartQuestion> testPartQuestions = new HashSet<>();

    @OneToMany(mappedBy = "testPart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TestPartQuestionSet> testPartQuestionSets = new HashSet<>();

    // Constructors
    public TestPart() {}

    public TestPart(Test test, Part part, Integer orderIndex) {
        this.test = test;
        this.part = part;
        this.orderIndex = orderIndex;
    }
}
