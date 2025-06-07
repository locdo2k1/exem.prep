package com.example.exam.prep.service;

import com.example.exam.prep.model.*;
import com.example.exam.prep.model.viewmodels.option.CreateQuestionOptionViewModel;
import com.example.exam.prep.model.viewmodels.question.CreateQuestionViewModel;
import com.example.exam.prep.repository.IQuestionRepository;
import com.example.exam.prep.repository.IQuestionTypeRepository;
import com.example.exam.prep.repository.IQuestionCategoryRepository;
import com.example.exam.prep.service.base.BaseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionService extends BaseService<Question> {
    private final IQuestionRepository questionRepository;
    private final IQuestionTypeRepository questionTypeRepository;
    private final IQuestionCategoryRepository questionCategoryRepository;

    @Autowired
    public QuestionService(
            IQuestionRepository questionRepository,
            IQuestionTypeRepository questionTypeRepository,
            IQuestionCategoryRepository questionCategoryRepository) {
        super(questionRepository);
        this.questionRepository = questionRepository;
        this.questionTypeRepository = questionTypeRepository;
        this.questionCategoryRepository = questionCategoryRepository;
    }

    @Override
    public Page<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    @Transactional
    public Question createQuestion(CreateQuestionViewModel createDto) {
        // Validate required fields
        if (createDto.getQuestionTypeId() == null) {
            throw new IllegalArgumentException("Question type ID is required");
        }

        // Fetch related entities
//        QuestionType questionType = questionTypeRepository.findById(createDto.getQuestionTypeId())
//                .orElseThrow(() -> new EntityNotFoundException("QuestionType not found with id: " + createDto.getQuestionTypeId()));

        // Create and map the question
        Question question = new Question();
//        question.setQuestionType(questionType);
        question.setPrompt(createDto.getPrompt());
        question.setScore(createDto.getScore());
        question.setScore(createDto.getScore());

        // Handle category if provided
//        if (createDto.getCategoryId() != null) {
//            QuestionCategory category = questionCategoryRepository.findById(createDto.getCategoryId())
//                    .orElseThrow(() -> new EntityNotFoundException("QuestionCategory not found with id: " + createDto.getCategoryId()));
//            question.setCategory(category);
//        }

        // Handle options if provided
        if (!createDto.getOptions().isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            CreateQuestionOptionViewModel[] options = null;
            try {
                options = mapper.readValue(createDto.getOptions(), CreateQuestionOptionViewModel[].class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            for (CreateQuestionOptionViewModel option : options) {
                Option createdOption = new Option();
                createdOption.setText(option.getText());
                createdOption.setCorrect(option.isCorrect());
                createdOption.setQuestion(question);
            }
        }

        return questionRepository.save(question);
    }

    @Transactional
    public Question updateQuestion(UUID id, CreateQuestionViewModel updateDto) {
        return questionRepository.findById(id)
                .map(existingQuestion -> {
                    // Update only the fields that should be updated
                    if (updateDto.getQuestionTypeId() != null) {
                        QuestionType questionType = questionTypeRepository.findById(updateDto.getQuestionTypeId())
                                .orElseThrow(() -> new EntityNotFoundException("QuestionType not found with id: " + updateDto.getQuestionTypeId()));
                        existingQuestion.setQuestionType(questionType);
                    }

                    if (updateDto.getCategoryId() != null) {
                        QuestionCategory category = questionCategoryRepository.findById(updateDto.getCategoryId())
                                .orElseThrow(() -> new EntityNotFoundException("QuestionCategory not found with id: " + updateDto.getCategoryId()));
                        existingQuestion.setCategory(category);
                    } else {
                        existingQuestion.setCategory(null);
                    }

                    // Map other fields from DTO to entity
                    existingQuestion.setId(id); // Ensure ID is preserved

                    return questionRepository.save(existingQuestion);
                })
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + id));
    }

}