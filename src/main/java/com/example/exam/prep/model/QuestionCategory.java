package com.example.exam.prep.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

/**
 * Represents a category of questions in the examination system.
 *
 * @example Sample categories:
 * <pre>
 * Category 1:
 *   - code: "PART1_PEOPLE"
 *   - skill: "Reading"
 *   - name: "[Part 1] Tranh tả người"
 *
 * Category 2:
 *   - code: "PART1_OBJECTS"
 *   - skill: "Reading"
 *   - name: "[Part 1] Tranh tả vật"
 * </pre>
 */
@Setter
@Getter
@Entity
@Table(name = "question_categories")
public class QuestionCategory extends BaseEntity {
    /**
     * Unique identifier code for the category.
     * @example "PART1_PEOPLE", "PART1_OBJECTS"
     */
    @Column(name = "code", nullable = false, unique = true, columnDefinition = "NVARCHAR(255)")
    private String code;

    /**
     * The skill being tested in this category.
     * @example "Reading"
     */
    @Column(name = "skill", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String skill;

    /**
     * The display name of the category.
     * @example "[Part 1] Tranh tả người", "[Part 1] Tranh tả vật"
     */
    @Column(name = "name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String name;

    /**
     * The set of questions belonging to this category.
     */
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "category")
    @JsonIgnore
    private Set<Question> questions;

    // Constructors
    public QuestionCategory() {}

    public QuestionCategory(String code, String skill, String name) {
        this.code = code;
        this.skill = skill;
        this.name = name;
    }
}
