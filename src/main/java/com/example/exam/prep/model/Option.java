package com.example.exam.prep.model;
import lombok.Data;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "options")
public class Option extends BaseEntity {
    @Column(nullable = false)
    private String text; // The text of the option

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question; // Reference back to the associated question

    @Column(nullable = false)
    private boolean correct; // Flag to indicate if this option is the correct answer
}

