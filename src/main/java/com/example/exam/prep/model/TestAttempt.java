package com.example.exam.prep.model;

import com.example.exam.prep.enums.TestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_attempts")
@Getter
@Setter
public class TestAttempt extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "total_score")
    private Double totalScore;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TestStatus status;
}