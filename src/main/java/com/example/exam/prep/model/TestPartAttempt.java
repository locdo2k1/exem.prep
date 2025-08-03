package com.example.exam.prep.model;

import com.example.exam.prep.constant.status.TestPartStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "test_part_attempts")
public class TestPartAttempt extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_attempt_id", nullable = false)
    private TestAttempt testAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_part_id", nullable = false)
    private TestPart testPart;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "score")
    private Double score;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TestPartStatus status;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;

    // Constructors
    public TestPartAttempt() {}

    public TestPartAttempt(TestAttempt testAttempt, TestPart testPart) {
        this.testAttempt = testAttempt;
        this.testPart = testPart;
        this.startTime = LocalDateTime.now();
        this.status = TestPartStatus.ONGOING;
    }
}
