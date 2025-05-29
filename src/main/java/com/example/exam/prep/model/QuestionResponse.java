package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "question_responses")
@Getter
@Setter
public class QuestionResponse extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_attempt_id", nullable = false)
    private TestAttempt testAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "text_answer", columnDefinition = "NVARCHAR(MAX)")
    private String textAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private QuestionOption selectedOption;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "question_response_selected_options",
        joinColumns = @JoinColumn(name = "response_id"),
        inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private Set<QuestionOption> selectedOptions;

    @Column(name = "score")
    private Double score;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "response_time", nullable = false)
    private LocalDateTime responseTime;
}