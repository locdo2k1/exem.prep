package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "tests")
public class Test extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private Set<TestPart> testParts;

    @ManyToMany
    @JoinTable(
        name = "test_questions",
        joinColumns = @JoinColumn(name = "test_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private Set<Question> questions;

    @ManyToMany
    @JoinTable(
        name = "test_question_sets",
        joinColumns = @JoinColumn(name = "test_id"),
        inverseJoinColumns = @JoinColumn(name = "question_set_id")
    )
    private Set<QuestionSet> questionSets;

    // Constructors
    public Test() {}

    public Test(String name, String description) {
        this.name = name;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
}
