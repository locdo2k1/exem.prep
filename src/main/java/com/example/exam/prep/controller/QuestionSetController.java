package com.example.exam.prep.controller;

import com.example.exam.prep.constant.response.QuestionSetResponseMessage;
import com.example.exam.prep.model.QuestionSet;
import com.example.exam.prep.model.viewmodels.questionset.QuestionSetCreateVM;
import com.example.exam.prep.model.viewmodels.questionset.QuestionSetUpdateVM;
import com.example.exam.prep.model.viewmodels.questionset.QuestionSetVM;
import com.example.exam.prep.model.viewmodels.questionset.QuestionSetSimpleVM;
import com.example.exam.prep.model.viewmodels.response.ApiResponse;
import com.example.exam.prep.service.IQuestionSetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@RestController
@RequestMapping("/api/question-sets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QuestionSetController {

        private final IQuestionSetService questionSetService;

        @GetMapping("/simple")
        public ResponseEntity<ApiResponse<Page<QuestionSetSimpleVM>>> getSimpleQuestionSets(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "title") String sort,
                        @RequestParam(defaultValue = "asc") String direction,
                        @RequestParam(required = false) String search) {

                // Parse sort parameter if it contains comma (e.g., "title,desc")
                String sortField = sort;
                String sortDir = direction;
                if (sort.contains(",")) {
                        String[] parts = sort.split(",");
                        sortField = parts[0].trim();
                        if (parts.length > 1) {
                                sortDir = parts[1].trim();
                        }
                }

                Sort.Direction sortDirection = "desc".equalsIgnoreCase(sortDir)
                                ? Sort.Direction.DESC
                                : Sort.Direction.ASC;

                Pageable pageable = PageRequest.of(
                                Math.max(0, page),
                                Math.max(1, size),
                                Sort.by(sortDirection, sortField));

                Page<QuestionSetSimpleVM> questionSets = questionSetService.findSimpleQuestionSets(search, pageable);

                return ResponseEntity.ok(ApiResponse.<Page<QuestionSetSimpleVM>>builder()
                                .success(true)
                                .message(QuestionSetResponseMessage.QUESTION_SETS_RETRIEVED.getMessage())
                                .data(questionSets)
                                .build());
        }

        @GetMapping
        public ResponseEntity<ApiResponse<Page<QuestionSetVM>>> getAllQuestionSets(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "title") String sort,
                        @RequestParam(defaultValue = "asc") String direction,
                        @RequestParam(required = false) String search) {

                // Parse sort parameter if it contains comma (e.g., "title,desc")
                String sortField = sort;
                String sortDir = direction;
                if (sort.contains(",")) {
                        String[] parts = sort.split(",");
                        sortField = parts[0].trim();
                        if (parts.length > 1) {
                                sortDir = parts[1].trim();
                        }
                }

                Sort.Direction sortDirection = "desc".equalsIgnoreCase(sortDir)
                                ? Sort.Direction.DESC
                                : Sort.Direction.ASC;

                Pageable pageable = PageRequest.of(
                                Math.max(0, page),
                                Math.max(1, size),
                                Sort.by(sortDirection, sortField));

                Page<QuestionSetVM> questionSetVMs = (search != null && !search.trim().isEmpty())
                                ? questionSetService.findQuestionSetVMsByTitleContaining(search.trim(), pageable)
                                : questionSetService.findAllQuestionSetVMs(pageable);

                return ResponseEntity.ok(
                                ApiResponse.success(questionSetVMs,
                                                QuestionSetResponseMessage.QUESTION_SETS_RETRIEVED.getMessage()));
        }

        @GetMapping("/{id}")
        @Transactional(readOnly = true)
        public ResponseEntity<ApiResponse<QuestionSetVM>> getQuestionSetById(@PathVariable UUID id) {
                QuestionSetVM questionSetVM = questionSetService.findById(id);
                return ResponseEntity
                                .ok(ApiResponse.success(questionSetVM,
                                                QuestionSetResponseMessage.QUESTION_SET_RETRIEVED.getMessage()));
        }

        @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
        public ResponseEntity<ApiResponse<QuestionSet>> createQuestionSet(
                        @Valid @ModelAttribute QuestionSetCreateVM questionSetVM) {

                try {
                        QuestionSet createdQuestionSet = questionSetService.createQuestionSet(questionSetVM);

                        return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(ApiResponse.success(createdQuestionSet,
                                                        QuestionSetResponseMessage.QUESTION_SET_CREATED.getMessage()));

                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest()
                                        .body(ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
                }
        }

        @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE,
                        MediaType.APPLICATION_JSON_VALUE })
        public ResponseEntity<ApiResponse<QuestionSetVM>> updateQuestionSet(
                        @PathVariable UUID id,
                        @Valid @ModelAttribute QuestionSetUpdateVM questionSetVM) {
                try {
                        // Set the ID from the path variable to ensure consistency
                        questionSetVM.setId(id);

                        QuestionSet updatedQuestionSet = questionSetService.updateQuestionSet(questionSetVM);
                        QuestionSetVM responseVM = questionSetService.findById(updatedQuestionSet.getId());

                        return ResponseEntity.ok(
                                        ApiResponse.success(responseVM,
                                                        QuestionSetResponseMessage.QUESTION_SET_UPDATED.getMessage()));
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest()
                                        .body(ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
                }
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> deleteQuestionSet(@PathVariable UUID id) {
                questionSetService.delete(id);
                return ResponseEntity.ok(
                                ApiResponse.success(null,
                                                QuestionSetResponseMessage.QUESTION_SET_DELETED.getMessage()));
        }
}
