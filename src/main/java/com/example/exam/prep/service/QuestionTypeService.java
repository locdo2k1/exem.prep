package com.example.exam.prep.service;

import com.example.exam.prep.model.QuestionType;
import com.example.exam.prep.repository.IQuestionTypeRepository;
import com.example.exam.prep.service.base.BaseService;
import org.springframework.stereotype.Service;

@Service
public class QuestionTypeService extends BaseService<QuestionType> {
    private final IQuestionTypeRepository questionTypeRepository;

    public QuestionTypeService(IQuestionTypeRepository questionTypeRepository) {
        super(questionTypeRepository);
        this.questionTypeRepository = questionTypeRepository;
    }
}

