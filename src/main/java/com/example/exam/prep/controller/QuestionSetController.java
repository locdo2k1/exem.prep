package com.example.exam.prep.controller;

import com.example.exam.prep.model.QuestionSet;
import com.example.exam.prep.model.viewmodels.response.ApiResponse;
import com.example.exam.prep.service.IQuestionSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<Page<QuestionSet>>> getAllQuestionSets(
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
            Sort.by(sortDirection, sort)
        );
        
        Page<QuestionSet> questionSets = (search != null && !search.trim().isEmpty())
                ? questionSetService.findByTitleContaining(search.trim(), pageable)
                : questionSetService.findAll(pageable);
                
        return ResponseEntity.ok(
            ApiResponse.success(questionSets, "Question sets retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionSet>> getQuestionSetById(@PathVariable UUID id) {
        return ResponseEntity.ok(
            ApiResponse.success(questionSetService.findById(id), "Question set retrieved successfully")
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<QuestionSet>> createQuestionSet(@RequestBody QuestionSet questionSet) {
        QuestionSet createdQuestionSet = questionSetService.create(questionSet);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdQuestionSet, "Question set created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionSet>> updateQuestionSet(
            @PathVariable UUID id, 
            @RequestBody QuestionSet questionSet) {
        
        if (!id.equals(questionSet.getId())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("ID in path and request body do not match", HttpStatus.BAD_REQUEST.value()));
        }
        
        QuestionSet updatedQuestionSet = questionSetService.update(questionSet);
        return ResponseEntity.ok(
                ApiResponse.success(updatedQuestionSet, "Question set updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestionSet(@PathVariable UUID id) {
        questionSetService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Question set deleted successfully")
        );
    }
}
