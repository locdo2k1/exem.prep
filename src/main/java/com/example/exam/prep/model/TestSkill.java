package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "test_skills")
public class TestSkill extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    // You can add additional fields here if needed in the future
    // For example: private Integer proficiencyLevel;
    
    // Constructors
    public TestSkill() {}

    public TestSkill(Test test, Skill skill) {
        this.test = test;
        this.skill = skill;
    }
}
