package com.example.exam.prep.controller;

import com.example.exam.prep.model.Test;
import com.example.exam.prep.service.ITestService;
import com.example.exam.prep.vm.test.TestVM;
import com.example.exam.prep.vm.test.TestVMSimple;
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
    @GetMapping("/simple")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<TestVMSimple>>> getAllTestsSimple(
            @org.springframework.data.web.PageableDefault(size = 10, sort = "name") org.springframework.data.domain.Pageable pageable,
            @RequestParam(value = "search", required = false) String search) {
        try {
            var page = testService.getAllTestsSimple(pageable, search
            );
            return ResponseEntity.ok(ApiResponse.success(page, com.example.exam.prep.constant.response.TestResponseMessage.TESTS_RETRIEVED.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving tests: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<TestVM>>> getAllTests(
            @org.springframework.data.web.PageableDefault(size = 10, sort = "name") org.springframework.data.domain.Pageable pageable,
            @RequestParam(value = "search", required = false) String search) {
        try {
            var page = testService.getAllTests(pageable, search);
            return ResponseEntity.ok(ApiResponse.success(page, com.example.exam.prep.constant.response.TestResponseMessage.TESTS_RETRIEVED.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving tests: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
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

        @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<ApiResponse<Test>> editTest(
                @PathVariable UUID id,
                @RequestPart("testData") String testVMString,
                @RequestPart(value = "files", required = false) List<MultipartFile> files) {
            try {
                // Debug: Log the incoming JSON
                System.out.println("Received test data for edit: " + testVMString);

                // Parse JSON to TestEditVM
                ObjectMapper objectMapper = new ObjectMapper();
                com.example.exam.prep.vm.test.TestEditVM testVM;
                try {
                    testVM = objectMapper.readValue(testVMString, com.example.exam.prep.vm.test.TestEditVM.class);
                    // Ensure the path variable id matches the VM id
                    testVM.setId(id);
                } catch (Exception e) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponse.error(
                                    "Invalid JSON format: " + e.getMessage(),
                                    HttpStatus.BAD_REQUEST.value()));
                }

                // Debug: Log parsed object
                System.out.println("Parsed TestEditVM: " + testVM.toString());

                // Process files if any
                if (files != null && !files.isEmpty()) {
                    System.out.println("Received " + files.size() + " file(s) for edit");
                    for (MultipartFile file : files) {
                        System.out.println("File: " + file.getOriginalFilename() +
                                " (" + file.getSize() + " bytes)");
                    }
                }

                // Process the request
                Test updatedTest = testService.editTest(testVM, files);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(ApiResponse.success(updatedTest,
                                TestResponseMessage.TEST_UPDATED.getMessage()));

            } catch (jakarta.persistence.EntityNotFoundException e) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(
                                TestResponseMessage.TEST_NOT_FOUND.getMessage(),
                                HttpStatus.NOT_FOUND.value()));
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
                                        ApiResponse.success(testVM, TestResponseMessage.TEST_RETRIEVED.getMessage()));
                } catch (jakarta.persistence.EntityNotFoundException e) {
                        return ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .body(ApiResponse.error(
                                                        TestResponseMessage.TEST_NOT_FOUND.getMessage(),
                                                        HttpStatus.NOT_FOUND.value()));
                }
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> deleteTest(@PathVariable UUID id) {
                try {
                        testService.deleteTest(id);
                        return ResponseEntity
                                        .status(HttpStatus.NO_CONTENT)
                                        .body(ApiResponse.success(null, TestResponseMessage.TEST_DELETED.getMessage()));
                } catch (jakarta.persistence.EntityNotFoundException e) {
                        return ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .body(ApiResponse.error(
                                                        TestResponseMessage.TEST_NOT_FOUND.getMessage(),
                                                        HttpStatus.NOT_FOUND.value()));
                } catch (Exception e) {
                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ApiResponse.error(
                                                        "Error deleting test: " + e.getMessage(),
                                                        HttpStatus.INTERNAL_SERVER_ERROR.value()));
                }
        }
}
