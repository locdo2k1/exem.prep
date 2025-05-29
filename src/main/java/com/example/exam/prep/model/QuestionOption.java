package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "question_options")
public class QuestionOption extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "question_set_id", nullable = false)
    private QuestionSet questionSet;

    @Column(name = "label", nullable = false, length = 1)
    private String label;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @ManyToMany(mappedBy = "correctOptions")
    private Set<Question> questions;

    // Constructors
    public QuestionOption() {}

    public QuestionOption(QuestionSet questionSet, String label, String description, String imageUrl) {
        this.questionSet = questionSet;
        this.label = label;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
