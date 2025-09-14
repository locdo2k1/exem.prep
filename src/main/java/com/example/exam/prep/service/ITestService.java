package com.example.exam.prep.service;

import com.example.exam.prep.model.Test;
import com.example.exam.prep.viewmodel.test.answer.TestAnswerVM;
import com.example.exam.prep.vm.test.TestCreateVM;
import com.example.exam.prep.vm.test.TestEditVM;
import com.example.exam.prep.vm.test.TestVM;
import com.example.exam.prep.vm.test.TestVMSimple;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import com.example.exam.prep.viewmodel.test.answer.FlattenedQuestionVM;

public interface ITestService {
        /**
         * Create a new test based on the provided view model and files
         * 
         * @param testVM The view model containing test creation data
         * @param files  Optional list of files to be associated with the test
         * @return The created Test entity
         * @throws IOException if there's an error processing the files
         */
        Test createTest(TestCreateVM testVM, List<MultipartFile> files) throws IOException;

        /**
         * Find a test by its ID and convert it to TestVM
         * 
         * @param id The ID of the test to find
         * @return The found test as TestVM
         * @throws jakarta.persistence.EntityNotFoundException if test not found
         */
        TestVM findById(UUID id);

        /**
         * Update an existing test based on the provided view model and files
         * 
         * @param testVM The view model containing test update data
         * @param files  Optional list of files to be associated with the test
         * @return The updated Test entity
         * @throws IOException                                 if there's an error
         *                                                     processing the files
         * @throws jakarta.persistence.EntityNotFoundException if test not found
         */
        Test editTest(TestEditVM testVM, List<MultipartFile> files) throws IOException;

        /**
         * Delete a test by its ID
         * 
         * @param id The ID of the test to delete
         * @throws jakarta.persistence.EntityNotFoundException if test not found
         */
        void deleteTest(UUID id);

        /**
         * Get all tests with paging and search
         * 
         * @param pageable the paging and sorting information
         * @param search   the search query (can be null or empty for all)
         * @return a page of TestVM matching the criteria
         */
        org.springframework.data.domain.Page<TestVM> getAllTests(org.springframework.data.domain.Pageable pageable,
                        String search);

        /**
         * Get all tests (simple) with paging and search
         * 
         * @param pageable the paging and sorting information
         * @param search   the search query (can be null or empty for all)
         * @return a page of TestVMSimple matching the criteria
         */
        org.springframework.data.domain.Page<TestVMSimple> getAllTestsSimple(
                        org.springframework.data.domain.Pageable pageable, String search);

        /**
         * Get answers for a specific test
         * 
         * @param testId The UUID of the test to get answers for
         * @return TestAnswerVM containing test information, parts, and questions with
         *         answers
         * @throws jakarta.persistence.EntityNotFoundException if test not found
         */
        TestAnswerVM testAnswers(UUID testId);
    
    /**
     * Get a flattened list of questions for a specific test part
     * 
     * @param partId The ID of the part
     * @param testId The ID of the test
     * @return A list of FlattenedQuestionVM objects
     */
    List<FlattenedQuestionVM> getFlattenedQuestionsForTestPart(UUID partId, UUID testId);
}
