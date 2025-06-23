package com.example.exam.prep.controller;

import com.example.exam.prep.model.Question;
import com.example.exam.prep.model.viewmodels.question.CreateQuestionViewModel;
import com.example.exam.prep.model.viewmodels.question.QuestionFilter;
import com.example.exam.prep.model.viewmodels.question.QuestionViewModel;
import com.example.exam.prep.model.viewmodels.question.UpdateQuestionViewModel;
import com.example.exam.prep.model.viewmodels.response.ApiResponse;
import com.example.exam.prep.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.example.exam.prep.constant.response.QuestionResponseMessage.*;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<QuestionViewModel>>> getAllQuestions(
            @RequestParam(required = false) UUID questionTypeId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) Integer minScore,
            @RequestParam(required = false) Integer maxScore,
            @RequestParam(required = false) Integer clipNumber,
            @RequestParam(required = false) String prompt,
            Pageable pageable) {
        
        QuestionFilter filter = new QuestionFilter();
        filter.setQuestionTypeId(questionTypeId);
        filter.setCategoryId(categoryId);
        filter.setMinScore(minScore);
        filter.setMaxScore(maxScore);
        filter.setClipNumber(clipNumber);
        filter.setPrompt(prompt);
        
        Page<QuestionViewModel> questions = questionService.findAll(filter, pageable);
        return ResponseEntity.ok(ApiResponse.success(questions, QUESTIONS_RETRIEVED.getMessage()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionViewModel>> getQuestionById(@PathVariable UUID id) {
        return questionService.findById(id)
                .map(question -> ResponseEntity.ok(ApiResponse.success(question, QUESTION_RETRIEVED.getMessage())))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(QUESTION_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND.value())));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Question>> createQuestion(
            @ModelAttribute CreateQuestionViewModel question) {
        try {
            Question savedQuestion = questionService.createQuestion(question);
            return ResponseEntity.ok(ApiResponse.success(savedQuestion, QUESTION_CREATED.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid input data", 400));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Question>> updateQuestion(
            @PathVariable UUID id,
            @ModelAttribute UpdateQuestionViewModel question) {
        return questionService.findById(id)
                .map(existingQuestion -> {
                    Question updatedQuestion = questionService.updateQuestion(id, question);
                    return ResponseEntity.ok(ApiResponse.success(updatedQuestion, QUESTION_UPDATED.getMessage()));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(QUESTION_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND.value())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable UUID id) {
        return questionService.findById(id)
                .map(question -> {
                    questionService.deleteById(id);
                    return ResponseEntity.ok(ApiResponse.<Void>success(null, QUESTION_DELETED.getMessage()));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(QUESTION_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND.value())));
    }
}