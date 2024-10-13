package com.example.exam.prep.controller;

import com.example.exam.prep.model.Question;
import com.example.exam.prep.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Question createQuestion(@RequestBody Question question, @RequestParam String insertedBy) {
        return questionService.createQuestion(question, insertedBy);
    }

    @PutMapping("/{id}")
    public Question updateQuestion(@PathVariable Long id, @RequestBody Question questionDetails, @RequestParam String updatedBy) {
        return questionService.updateQuestion(id, questionDetails, updatedBy);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteQuestion(@PathVariable Long id) {
        questionService.softDeleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
