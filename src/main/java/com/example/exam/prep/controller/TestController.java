package com.example.exam.prep.controller;

import com.example.exam.prep.model.Test;
import com.example.exam.prep.service.ITestService;
import com.example.exam.prep.vm.test.TestVM;
import com.example.exam.prep.constant.response.TestResponseMessage;
import com.example.exam.prep.model.viewmodels.response.ApiResponse;
import com.example.exam.prep.vm.test.TestCreateVM;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tests")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TestController {
        private final ITestService testService;

        @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<ApiResponse<Test>> createTest(
                        @RequestPart("testData") String testVMString,
                        @RequestPart(value = "files", required = false) List<MultipartFile> files) {
                try {
                        // Debug: Log the incoming JSON
                        System.out.println("Received test data: " + testVMString);

                        // Parse JSON to TestCreateVM
                        ObjectMapper objectMapper = new ObjectMapper();
                        TestCreateVM testVM;
                        try {
                                testVM = objectMapper.readValue(testVMString, TestCreateVM.class);
                        } catch (Exception e) {
                                return ResponseEntity
                                                .status(HttpStatus.BAD_REQUEST)
                                                .body(ApiResponse.error(
                                                                "Invalid JSON format: " + e.getMessage(),
                                                                HttpStatus.BAD_REQUEST.value()));
                        }

                        // Debug: Log parsed object
                        System.out.println("Parsed TestCreateVM: " + testVM.toString());

                        // Process files if any
                        if (files != null && !files.isEmpty()) {
                                System.out.println("Received " + files.size() + " file(s)");
                                for (MultipartFile file : files) {
                                        System.out.println("File: " + file.getOriginalFilename() +
                                                        " (" + file.getSize() + " bytes)");
                                }
                        }

                        // Process the request
                        Test createdTest = testService.createTest(testVM, files);
                        return ResponseEntity
                                        .status(HttpStatus.CREATED)
                                        .body(ApiResponse.success(createdTest,
                                                        TestResponseMessage.TEST_CREATED.getMessage()));

                } catch (Exception e) {
                        // Log the full exception for debugging
                        e.printStackTrace();
                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.error(
                                                        "Error processing request: " + e.getMessage(),
                                                        HttpStatus.INTERNAL_SERVER_ERROR.value()));
                }
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<TestVM>> getTestById(@PathVariable UUID id) {
                try {
                        TestVM testVM = testService.findById(id);
                        return ResponseEntity.ok(
                                ApiResponse.success(testVM, TestResponseMessage.TEST_RETRIEVED.getMessage())
                        );
                } catch (jakarta.persistence.EntityNotFoundException e) {
                        return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.error(
                                        TestResponseMessage.TEST_NOT_FOUND.getMessage(),
                                        HttpStatus.NOT_FOUND.value()
                                ));
                }
        }

        // TODO: Uncomment and implement these methods as needed
        /*
         * @GetMapping
         * public ResponseEntity<ApiResponse<Page<Test>>> getAllTests(
         * 
         * @RequestParam(required = false) String title,
         * 
         * @RequestParam(required = false) UUID testTypeId,
         * Pageable pageable) {
         * // Implementation for getting paginated tests with filtering
         * throw new UnsupportedOperationException("Not implemented yet");
         * }
         * 
         * @PutMapping("/{id}")
         * public ResponseEntity<ApiResponse<Test>> updateTest(
         * 
         * @PathVariable UUID id,
         * 
         * @RequestBody TestUpdateVM testVM) {
         * // Implementation for updating a test
         * throw new UnsupportedOperationException("Not implemented yet");
         * }
         * 
         * @DeleteMapping("/{id}")
         * public ResponseEntity<ApiResponse<Void>> deleteTest(@PathVariable UUID id) {
         * // Implementation for deleting a test
         * throw new UnsupportedOperationException("Not implemented yet");
         * }
         */
}
