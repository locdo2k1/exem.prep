package com.example.exam.prep.repository;

import com.example.exam.prep.model.TestPartAttempt;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link com.example.exam.prep.model.TestPartAttempt} entities.
 * Handles database operations for test part attempts.
 */
@Repository
public interface ITestPartAttemptRepository extends GenericRepository<TestPartAttempt> {
    
    /**
     * Finds all test part attempts for a specific test attempt.
     *
     * @param testAttemptId The ID of the test attempt
     * @return List of test part attempts
     */
    @Query("SELECT tpa FROM TestPartAttempt tpa WHERE tpa.testAttempt.id = :testAttemptId")
    List<TestPartAttempt> findByTestAttemptId(@Param("testAttemptId") UUID testAttemptId);
    
    /**
     * Finds all test part attempts for a specific test part.
     *
     * @param testPartId The ID of the test part
     * @return List of test part attempts
     */
    @Query("SELECT tpa FROM TestPartAttempt tpa WHERE tpa.part.id = :partId")
    List<TestPartAttempt> findByPartId(@Param("partId") UUID partId);
    
    /**
     * Finds the latest test part attempt for a specific test part and user.
     *
     * @param testPartId The ID of the test part
     * @return The latest test part attempt, if found
     */
    @Query("SELECT tpa FROM TestPartAttempt tpa " +
           "WHERE tpa.part.id = :partId")
    List<TestPartAttempt> findLatestByPartAndUser(
        @Param("partId") UUID partId
    );
}
