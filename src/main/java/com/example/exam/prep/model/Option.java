package com.example.exam.prep.model;
import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
@Table(name = "options")
public class Option extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text; // The text of the option

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question; // Reference back to the associated question

    @Column(nullable = false)
    private boolean isCorrect; // Flag to indicate if this option is the correct answer
}

