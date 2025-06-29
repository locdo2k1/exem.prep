package com.example.exam.prep.service;

import com.example.exam.prep.model.QuestionSet;
import com.example.exam.prep.model.viewmodels.questionset.QuestionSetVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.exam.prep.model.viewmodels.questionset.QuestionSetCreateVM;

import java.util.List;
import java.util.UUID;

public interface IQuestionSetService {
    
    QuestionSet create(QuestionSet questionSet);
    
    QuestionSet update(QuestionSet questionSet);
    
    void delete(UUID id);
    
    QuestionSet findById(UUID id);
    
    List<QuestionSet> findAll();
    
    Page<QuestionSet> findAll(Pageable pageable);
    
    Page<QuestionSet> findByTitleContaining(String title, Pageable pageable);
    
    QuestionSet createQuestionSet(QuestionSetCreateVM questionSetVM);
    
    // New methods that return VM objects
    Page<QuestionSetVM> findAllQuestionSetVMs(Pageable pageable);
    
    Page<QuestionSetVM> findQuestionSetVMsByTitleContaining(String title, Pageable pageable);
    
    // Add any additional custom methods specific to QuestionSet here
}
