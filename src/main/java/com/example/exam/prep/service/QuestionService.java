package com.example.exam.prep.service;

import com.example.exam.prep.model.Question;
import com.example.exam.prep.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    public Question createQuestion(Question question, String insertedBy) {
        question.setInsertedBy(insertedBy);
        return questionRepository.save(question);
    }

    public Question updateQuestion(Long id, Question questionDetails, String updatedBy) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        question.setText(questionDetails.getText());
        question.setOptions(questionDetails.getOptions());
        question.setCorrectAnswer(questionDetails.getCorrectAnswer());
        question.setCategory(questionDetails.getCategory());
        question.setDifficultyLevel(questionDetails.getDifficultyLevel());
        return questionRepository.save(question);
    }

    public void softDeleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        questionRepository.save(question);
    }
}

