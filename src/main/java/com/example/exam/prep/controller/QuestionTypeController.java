package com.example.exam.prep.controller;

import com.example.exam.prep.model.QuestionType;
import com.example.exam.prep.model.viewmodels.response.ApiResponse;
import com.example.exam.prep.service.QuestionTypeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.example.exam.prep.constant.response.QuestionTypeResponseMessage.*;

@RestController
@RequestMapping("/api/question-types")
@CrossOrigin(origins = "*")
public class QuestionTypeController {
    private final QuestionTypeService questionTypeService;

    public QuestionTypeController(QuestionTypeService questionTypeService) {
        this.questionTypeService = questionTypeService;
    }

    /**
     * GET /api/question-types : Get all question types with pagination and search
     *
     * @param page the page number (0-based), defaults to 0
     * @param size the page size, defaults to 10
     * @param sort the sort column, defaults to 'name'
     * @param direction the sort direction, defaults to 'asc'
     * @param search the search keyword (searches in name field)
     * @return the ApiResponse containing paginated question types
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<QuestionType>>> getAllQuestionTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search) {
        
        // Decode the search parameter if it exists
        String decodedSearch = null;
        if (search != null && !search.trim().isEmpty()) {
            try {
                decodedSearch = URLDecoder.decode(search.trim(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                // If decoding fails, use the original search string
                decodedSearch = search.trim();
            }
        }
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
            
        Pageable pageable = PageRequest.of(
            page, 
            size, 
            Sort.by(sortDirection, sort)
        );
        
        Page<QuestionType> questionTypes = (decodedSearch != null)
            ? questionTypeService.search(decodedSearch, pageable)
            : questionTypeService.findAll(pageable);
            
        return ResponseEntity.ok(ApiResponse.success(questionTypes, QUESTION_TYPES_RETRIEVED.getMessage()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionType>> getQuestionTypeById(@PathVariable UUID id) {
        return questionTypeService.findById(id)
                .map(questionType -> ResponseEntity.ok(ApiResponse.success(questionType, QUESTION_TYPE_RETRIEVED.getMessage())))
                .orElse(ResponseEntity.ok(ApiResponse.error(getNotFoundMessage(), 404)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<QuestionType>> createQuestionType(
            @RequestBody QuestionType questionType) {
        QuestionType savedQuestionType = questionTypeService.save(questionType);
        return ResponseEntity.ok(ApiResponse.success(savedQuestionType, QUESTION_TYPE_CREATED.getMessage()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionType>> updateQuestionType(
            @PathVariable UUID id,
            @RequestBody QuestionType questionType) {
        return questionTypeService.findById(id)
                .map(existingType -> {
                    questionType.setId(id);
                    QuestionType updatedType = questionTypeService.save(questionType);
                    return ResponseEntity.ok(ApiResponse.success(updatedType, QUESTION_TYPE_UPDATED.getMessage()));
                })
                .orElse(ResponseEntity.ok(ApiResponse.error(getNotFoundMessage(), 404)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestionType(@PathVariable UUID id) {
        return questionTypeService.findById(id)
                .map(questionType -> {
                    questionTypeService.deleteById(id);
                    return ResponseEntity.ok(ApiResponse.<Void>success(null, QUESTION_TYPE_DELETED.getMessage()));
                })
                .orElse(ResponseEntity.ok(ApiResponse.error(getNotFoundMessage(), 404)));
    }

    private String getNotFoundMessage() {
        return QUESTION_TYPE_NOT_FOUND.getMessage();
    }
}
