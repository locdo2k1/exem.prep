package com.example.exam.prep.repository;

import com.example.exam.prep.model.TestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TestAttemptRepository extends JpaRepository<TestAttempt, UUID> {
    List<TestAttempt> findByTestId(UUID testId);
    List<TestAttempt> findByTestIdAndUserId(UUID testId, UUID userId);
}
