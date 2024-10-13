package com.example.exam.prep.model;
import lombok.Data;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "questions")
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text; // The text of the question

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options; // A list of answer options

    @Column(nullable = false)
    private String correctAnswer; // The correct answer

    private String category; // Optional: to categorize questions (e.g., Math, Science)

    private int difficultyLevel; // Optional: to indicate difficulty level (1-5)
}

