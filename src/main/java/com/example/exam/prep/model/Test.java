package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "tests")
public class Test extends BaseEntity {
    @Column(name = "number", nullable = false, unique = true, insertable = false, updatable = false)
    private Integer number;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "skill")
    private String skill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_category_id")
    private TestCategory testCategory;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private Set<TestPart> testParts;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TestFile> testFiles = new java.util.HashSet<>();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TestQuestionDetail> testQuestionDetails = new java.util.HashSet<>();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TestQuestionSetDetail> testQuestionSetDetails = new java.util.HashSet<>();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TestSkill> testSkills = new java.util.HashSet<>();

    // Constructors
    public Test() {}

    public Test(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
