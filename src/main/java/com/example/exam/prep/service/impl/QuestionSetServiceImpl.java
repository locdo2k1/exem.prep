package com.example.exam.prep.service.impl;

import com.example.exam.prep.model.QuestionSet;
import com.example.exam.prep.service.IQuestionSetService;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionSetServiceImpl implements IQuestionSetService {

    private final IUnitOfWork unitOfWork;

    @Override
    @Transactional
    public QuestionSet create(QuestionSet questionSet) {
        return unitOfWork.getQuestionSetRepository().save(questionSet);
    }

    @Override
    @Transactional
    public QuestionSet update(QuestionSet questionSet) {
        if (!unitOfWork.getQuestionSetRepository().existsById(questionSet.getId())) {
            throw new EntityNotFoundException("QuestionSet not found with id: " + questionSet.getId());
        }
        return unitOfWork.getQuestionSetRepository().save(questionSet);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        QuestionSet questionSet = unitOfWork.getQuestionSetRepository().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("QuestionSet not found with id: " + id));
        unitOfWork.getQuestionSetRepository().delete(questionSet);
    }

    @Override
    public QuestionSet findById(UUID id) {
        return unitOfWork.getQuestionSetRepository().findById(id)
                .orElseThrow(() -> new EntityNotFoundException("QuestionSet not found with id: " + id));
    }

    @Override
    public List<QuestionSet> findAll() {
        return unitOfWork.getQuestionSetRepository().findAll();
    }

    @Override
    public Page<QuestionSet> findAll(Pageable pageable) {
        return unitOfWork.getQuestionSetRepository().findAll(pageable);
    }

    @Override
    public Page<QuestionSet> findByTitleContaining(String title, Pageable pageable) {
        return unitOfWork.getQuestionSetRepository().findByTitleContainingIgnoreCaseAndIsDeletedFalse(title, pageable);
    }
}
