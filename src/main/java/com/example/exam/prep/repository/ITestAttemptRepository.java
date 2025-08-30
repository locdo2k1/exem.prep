package com.example.exam.prep.repository;

import com.example.exam.prep.model.TestAttempt;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ITestAttemptRepository extends GenericRepository<TestAttempt> {

        /**
         * Find all attempts for a specific test
         * 
         * @param testId The ID of the test
         * @return List of test attempts
         */
        List<TestAttempt> findByTestId(UUID testId);

        /**
         * Find all attempts for a specific test and user
         * 
         * @param testId The ID of the test
         * @param userId The ID of the user
         * @return List of test attempts
         */
        List<TestAttempt> findByTestIdAndUserId(UUID testId, UUID userId);

        /**
         * Count distinct users who have attempted a specific test.
         *
         * @param testId The ID of the test
         * @return Number of distinct users who attempted the test
         */
        @Query("SELECT COUNT(DISTINCT ta.user.id) FROM TestAttempt ta WHERE ta.test.id = :testId")
        int countDistinctUsersByTestId(@Param("testId") UUID testId);

        /**
         * Find a test attempt by its ID and user ID.
         *
         * @param attemptId The ID of the test attempt
         * @param userId    The ID of the user
         * @return Optional containing the test attempt if found
         */
        @Query("SELECT ta FROM TestAttempt ta WHERE ta.id = :attemptId AND ta.user.id = :userId")
        Optional<TestAttempt> findByIdAndUserId(
                        @Param("attemptId") UUID attemptId,
                        @Param("userId") UUID userId);
}
