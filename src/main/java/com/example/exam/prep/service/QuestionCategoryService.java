package com.example.exam.prep.service;

import com.example.exam.prep.model.QuestionCategory;
import com.example.exam.prep.repository.IQuestionCategoryRepository;
import com.example.exam.prep.service.base.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionCategoryService extends BaseService<QuestionCategory> {
    private final IQuestionCategoryRepository questionCategoryRepository;

    public QuestionCategoryService(IQuestionCategoryRepository questionCategoryRepository) {
        super(questionCategoryRepository);
        this.questionCategoryRepository = questionCategoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<QuestionCategory> search(String keyword, Pageable pageable) {

        return questionCategoryRepository.findByNameContaining(keyword, pageable);
    }
}

