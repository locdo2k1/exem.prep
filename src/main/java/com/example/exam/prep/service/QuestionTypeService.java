package com.example.exam.prep.service;

import com.example.exam.prep.model.QuestionType;
import com.example.exam.prep.repository.IQuestionTypeRepository;
import com.example.exam.prep.service.base.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionTypeService extends BaseService<QuestionType> {
    private final IQuestionTypeRepository questionTypeRepository;

    public QuestionTypeService(IQuestionTypeRepository questionTypeRepository) {
        super(questionTypeRepository);
        this.questionTypeRepository = questionTypeRepository;
    }

    @Transactional(readOnly = true)
    public Page<QuestionType> search(String keyword, Pageable pageable) {
        return questionTypeRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
}

