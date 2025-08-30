package com.example.exam.prep.model;

import com.example.exam.prep.constant.status.TestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "test_attempts")
@Getter
@Setter
public class TestAttempt extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "total_score")
    private Double totalScore;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TestStatus status;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "is_practice", nullable = false, columnDefinition = "boolean default false")
    private Boolean isPractice = false;

    @OneToMany(mappedBy = "testAttempt", cascade = CascadeType.ALL)
    private Set<TestPartAttempt> testPartAttempts = new HashSet<>();
}