package com.example.exam.prep.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.exam.prep.model.viewmodels.PracticeTestInfoVM;
import com.example.exam.prep.model.viewmodels.TestAttemptInfoVM;
import com.example.exam.prep.model.viewmodels.TestListItemVM;

public interface ITestInfoService {
    /**
     * Get practice test information for a specific test by ID
     * This includes part information, statistics, and other details needed for the
     * practice interface
     * 
     * @param testId The ID of the test to retrieve information for
     * @return PracticeTestInfoVM with test details for practice mode
     */
    PracticeTestInfoVM getPracticeTestInfo(UUID testId);

    /**
     * Get a list of all available tests for practice mode
     * 
     * @return List of test information specifically formatted for practice mode
     * @deprecated Use ITestService.getAllTests or getAllTestsSimple instead
     */
    @Deprecated
    String getAllTests();

    /**
     * Get test attempts for a specific test and optionally a specific user
     * 
     * @param testId The ID of the test
     * @param userId Optional user ID to filter attempts
     * @param tz Optional IANA timezone ID (e.g., "Asia/Ho_Chi_Minh") to format local takeDate
     * @return List of test attempts with details
     */
    List<TestAttemptInfoVM> getTestAttempts(UUID testId, UUID userId, String tz);

    /**
     * Get paginated list of tests with filtering by test category and search keyword
     * 
     * @param testCategoryId Optional test category ID to filter tests
     * @param keyword Optional keyword to search in test name
     * @param pageable Pagination information
     * @return Page of TestListItemVM with test information
     */
    Page<TestListItemVM> getTestList(UUID testCategoryId, String keyword, Pageable pageable);
}
