package com.example.exam.prep.vm.testattempt;

import com.example.exam.prep.constant.status.TestStatus;
import com.example.exam.prep.vm.test.TestSimpleVM;
import com.example.exam.prep.vm.user.UserSimpleVM;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestAttemptVM {
    private UUID id;
    private TestSimpleVM test;
    private UserSimpleVM user;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double totalScore;
    private TestStatus status;
}
