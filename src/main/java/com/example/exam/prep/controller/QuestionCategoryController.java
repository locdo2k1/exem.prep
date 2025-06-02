package com.example.exam.prep.controller;

import com.example.exam.prep.constant.response.QuestionCategoryResponseMessage;
import com.example.exam.prep.model.QuestionCategory;
import com.example.exam.prep.model.viewmodels.response.ApiResponse;
import com.example.exam.prep.service.QuestionCategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing question categories.
 */
@RestController
@RequestMapping("/api/question-categories")
public class QuestionCategoryController {

    private final QuestionCategoryService categoryService;

    public QuestionCategoryController(QuestionCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * GET /api/question-categories : Get all question categories with pagination and search
     *
     * @param page the page number (0-based), defaults to 0
     * @param size the page size, defaults to 10
     * @param sort the sort column, defaults to 'name'
     * @param direction the sort direction, defaults to 'asc'
     * @param search the search keyword (searches in name and code fields)
     * @return the ApiResponse containing paginated categories
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<QuestionCategory>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search) {
        
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
            
        Pageable pageable = PageRequest.of(
            page, 
            size, 
            Sort.by(sortDirection, sort)
        );
        
        Page<QuestionCategory> categories = (search != null && !search.trim().isEmpty())
            ? categoryService.search(search, pageable)
            : categoryService.findAll(pageable);
            
        return ResponseEntity.ok(ApiResponse.success(categories,
            QuestionCategoryResponseMessage.QUESTION_CATEGORIES_RETRIEVED.getMessage()));
    }

    /**
     * GET /api/question-categories/{id} : Get a question category by ID
     *
     * @param id the ID of the question category to retrieve
     * @return the ApiResponse containing the question category if found, or an error response
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionCategory>> getById(@PathVariable UUID id) {
        return categoryService.findById(id)
                .map(category -> ResponseEntity.ok(ApiResponse.success(category,
                    QuestionCategoryResponseMessage.QUESTION_CATEGORY_RETRIEVED.getMessage())))
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(
                            QuestionCategoryResponseMessage.QUESTION_CATEGORY_NOT_FOUND.formatMessage(id),
                            HttpStatus.NOT_FOUND.value())));
    }

    /**
     * POST /api/question-categories : Create a new question category
     *
     * @param category the question category to create
     * @return the ApiResponse containing the created question category
     */
    @PostMapping
    public ResponseEntity<ApiResponse<QuestionCategory>> create(@RequestBody QuestionCategory category) {
        QuestionCategory savedCategory = categoryService.save(category);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(savedCategory,
                    QuestionCategoryResponseMessage.QUESTION_CATEGORY_CREATED.getMessage()));
    }

    /**
     * PUT /api/question-categories/{id} : Update an existing question category
     *
     * @param id the ID of the question category to update
     * @param category the updated question category
     * @return the ApiResponse containing the updated question category if found, or an error response
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionCategory>> update(
            @PathVariable UUID id,
            @RequestBody QuestionCategory category) {
        return categoryService.findById(id)
                .map(existing -> {
                    category.setId(id);
                    QuestionCategory updatedCategory = categoryService.save(category);
                    return ResponseEntity.ok(ApiResponse.success(updatedCategory,
                        QuestionCategoryResponseMessage.QUESTION_CATEGORY_UPDATED.getMessage()));
                })
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(
                            QuestionCategoryResponseMessage.QUESTION_CATEGORY_NOT_FOUND.formatMessage(id),
                            HttpStatus.NOT_FOUND.value())));
    }

    /**
     * DELETE /api/question-categories/{id} : Delete a question category by ID
     *
     * @param id the ID of the question category to delete
     * @return the ApiResponse with success message if successful,
     *         or error message if the question category doesn't exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return categoryService.findById(id)
                .map(existing -> {
                    categoryService.deleteById(id);
                    return ResponseEntity.ok(ApiResponse.<Void>success(null,
                        QuestionCategoryResponseMessage.QUESTION_CATEGORY_DELETED.getMessage()));
                })
                .orElse(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Void>error(
                            QuestionCategoryResponseMessage.QUESTION_CATEGORY_NOT_FOUND.formatMessage(id),
                            HttpStatus.NOT_FOUND.value())));
    }
}
