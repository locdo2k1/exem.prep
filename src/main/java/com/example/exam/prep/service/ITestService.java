package com.example.exam.prep.service;

import com.example.exam.prep.model.Test;
import com.example.exam.prep.vm.test.TestCreateVM;
import com.example.exam.prep.vm.test.TestVM;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ITestService {
    /**
     * Create a new test based on the provided view model and files
     * @param testVM The view model containing test creation data
     * @param files Optional list of files to be associated with the test
     * @return The created Test entity
     * @throws IOException if there's an error processing the files
     */
    Test createTest(TestCreateVM testVM, List<MultipartFile> files) throws IOException;
    
    /**
     * Find a test by its ID and convert it to TestVM
     * @param id The ID of the test to find
     * @return The found test as TestVM
     * @throws jakarta.persistence.EntityNotFoundException if test not found
     */
    TestVM findById(UUID id);
}
