package com.example.exam.prep.service;

import com.example.exam.prep.model.QuestionSet;
import com.example.exam.prep.model.viewmodels.questionset.QuestionSetVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.exam.prep.model.viewmodels.questionset.QuestionSetCreateVM;
import com.example.exam.prep.model.viewmodels.questionset.QuestionSetUpdateVM;

import java.util.List;
import java.util.UUID;

public interface IQuestionSetService {
    
    QuestionSet create(QuestionSet questionSet);
    
    QuestionSet update(QuestionSet questionSet);
    
    void delete(UUID id);
    
    QuestionSetVM findById(UUID id);
    
    List<QuestionSet> findAll();
    
    Page<QuestionSet> findAll(Pageable pageable);
    
    Page<QuestionSet> findByTitleContaining(String title, Pageable pageable);
    
    QuestionSet createQuestionSet(QuestionSetCreateVM questionSetVM);
    
    // New methods that return VM objects
    Page<QuestionSetVM> findAllQuestionSetVMs(Pageable pageable);
    
    Page<QuestionSetVM> findQuestionSetVMsByTitleContaining(String title, Pageable pageable);
    
    /**
     * Updates an existing QuestionSet using the provided view model
     * @param questionSetVM The view model containing the updated data
     * @return The updated QuestionSet entity
     * @throws IllegalArgumentException if the QuestionSet is not found or validation fails
     */
    QuestionSet updateQuestionSet(QuestionSetUpdateVM questionSetVM);
}
