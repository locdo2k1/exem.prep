package com.example.exam.prep.service;

import com.example.exam.prep.model.Question;
import com.example.exam.prep.repository.IQuestionRepository;
import com.example.exam.prep.service.base.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class QuestionService extends BaseService<Question> {
    private final IQuestionRepository questionRepository;

    public QuestionService(IQuestionRepository questionRepository) {
        super(questionRepository);
        this.questionRepository = questionRepository;
    }

    public Page<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }
}