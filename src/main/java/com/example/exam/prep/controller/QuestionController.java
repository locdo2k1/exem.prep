package com.example.exam.prep.controller;

import com.example.exam.prep.model.Question;
import com.example.exam.prep.model.viewmodels.ApiResponse;
import com.example.exam.prep.service.QuestionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.example.exam.prep.constant.response.QuestionResponseMessage.*;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Question>>> getAllQuestions(Pageable pageable) {
        Page<Question> questions = questionService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(questions, QUESTIONS_RETRIEVED.getMessage()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Question>> getQuestionById(@PathVariable Long id) {
        return questionService.findById(id)
                .map(question -> ResponseEntity.ok(ApiResponse.success(question, QUESTION_RETRIEVED.getMessage())))
                .orElse(ResponseEntity.ok(ApiResponse.error(getNotFoundMessage(), 404)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Question>> createQuestion(@RequestBody Question question) {
        Question savedQuestion = questionService.save(question);
        return ResponseEntity.ok(ApiResponse.success(savedQuestion, QUESTION_CREATED.getMessage()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Question>> updateQuestion(
            @PathVariable Long id,
            @RequestBody Question question) {
        return questionService.findById(id)
                .map(existingQuestion -> {
                    question.setId(id);
                    Question updatedQuestion = questionService.save(question);
                    return ResponseEntity.ok(ApiResponse.success(updatedQuestion, QUESTION_UPDATED.getMessage()));
                })
                .orElse(ResponseEntity.ok(ApiResponse.error(getNotFoundMessage(), 404)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable Long id) {
        return questionService.findById(id)
                .map(question -> {
                    questionService.deleteById(id);
                    return ResponseEntity.ok(ApiResponse.<Void>success(null, QUESTION_DELETED.getMessage()));
                })
                .orElse(ResponseEntity.ok(ApiResponse.error(getNotFoundMessage(), 404)));
    }
}