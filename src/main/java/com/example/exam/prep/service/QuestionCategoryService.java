package com.example.exam.prep.service;

import com.example.exam.prep.model.QuestionCategory;
import com.example.exam.prep.repository.IQuestionCategoryRepository;
import com.example.exam.prep.service.base.BaseService;
import org.springframework.stereotype.Service;

@Service
public class QuestionCategoryService extends BaseService<QuestionCategory> {
    private final IQuestionCategoryRepository questionCategoryRepository;

    public QuestionCategoryService(IQuestionCategoryRepository questionCategoryRepository) {
        super(questionCategoryRepository);
        this.questionCategoryRepository = questionCategoryRepository;
    }
}

