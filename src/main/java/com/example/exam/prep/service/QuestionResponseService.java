package com.example.exam.prep.service;

import com.example.exam.prep.model.QuestionResponse;
import com.example.exam.prep.repository.IQuestionResponseRepository;
import com.example.exam.prep.service.base.BaseService;
import org.springframework.stereotype.Service;

@Service
public class QuestionResponseService extends BaseService<QuestionResponse> {
    private final IQuestionResponseRepository questionResponseRepository;

    public QuestionResponseService(IQuestionResponseRepository questionResponseRepository) {
        super(questionResponseRepository);
        this.questionResponseRepository = questionResponseRepository;
    }
}

