package com.example.exam.prep.controller;

import com.example.exam.prep.constant.response.QuestionSetResponseMessage;
import com.example.exam.prep.model.QuestionSet;
import com.example.exam.prep.model.viewmodels.questionset.QuestionSetCreateVM;
import com.example.exam.prep.model.viewmodels.questionset.QuestionSetVM;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/question-sets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QuestionSetController {

    private final IQuestionSetService questionSetService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<QuestionSetVM>>> getAllQuestionSets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search) {

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.max(1, size),
                Sort.by(sortDirection, sort));

        Page<QuestionSetVM> questionSetVMs = (search != null && !search.trim().isEmpty())
                ? questionSetService.findQuestionSetVMsByTitleContaining(search.trim(), pageable)
                : questionSetService.findAllQuestionSetVMs(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(questionSetVMs, QuestionSetResponseMessage.QUESTION_SETS_RETRIEVED.getMessage()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionSet>> getQuestionSetById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.success(questionSetService.findById(id),
                        QuestionSetResponseMessage.QUESTION_SET_RETRIEVED.getMessage()));
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
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

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionSet>> updateQuestionSet(
            @PathVariable UUID id,
            @RequestBody QuestionSet questionSet) {

        if (!id.equals(questionSet.getId())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(QuestionSetResponseMessage.ID_MISMATCH.getMessage(),
                            HttpStatus.BAD_REQUEST.value()));
        }

        QuestionSet updatedQuestionSet = questionSetService.update(questionSet);
        return ResponseEntity.ok(
                ApiResponse.success(updatedQuestionSet, QuestionSetResponseMessage.QUESTION_SET_UPDATED.getMessage()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestionSet(@PathVariable UUID id) {
        questionSetService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, QuestionSetResponseMessage.QUESTION_SET_DELETED.getMessage()));
    }
}
