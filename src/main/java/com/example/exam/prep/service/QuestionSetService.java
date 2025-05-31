package com.example.exam.prep.service;

import com.example.exam.prep.model.QuestionSet;
import com.example.exam.prep.repository.IQuestionSetRepository;
import com.example.exam.prep.service.base.BaseService;
import org.springframework.stereotype.Service;

@Service
public class QuestionSetService extends BaseService<QuestionSet> {
    private final IQuestionSetRepository questionSetRepository;

    public QuestionSetService(IQuestionSetRepository questionSetRepository) {
        super(questionSetRepository);
        this.questionSetRepository = questionSetRepository;
    }
}

