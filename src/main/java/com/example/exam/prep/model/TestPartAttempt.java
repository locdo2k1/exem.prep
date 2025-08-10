package com.example.exam.prep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "test_part_attempts")
public class TestPartAttempt extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_attempt_id", nullable = false)
    private TestAttempt testAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    // Constructors
    public TestPartAttempt() {}

    public TestPartAttempt(TestAttempt testAttempt, Part part) {
        this.testAttempt = testAttempt;
        this.part = part;
    }
}
