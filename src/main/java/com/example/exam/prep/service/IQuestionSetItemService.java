package com.example.exam.prep.service;

import com.example.exam.prep.model.QuestionSetItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IQuestionSetItemService {
    
    // Standard CRUD operations
    QuestionSetItem create(QuestionSetItem questionSetItem);
    
    QuestionSetItem update(QuestionSetItem questionSetItem);
    
    void delete(UUID id);
    
    QuestionSetItem findById(UUID id);
    
    List<QuestionSetItem> findAll();
    
    Page<QuestionSetItem> findAll(Pageable pageable);
    
    // Custom methods
    QuestionSetItem getQuestionSetItemById(UUID id);
    
    List<QuestionSetItem> getItemsByQuestionSetId(UUID questionSetId);
    
    List<QuestionSetItem> getActiveItemsByQuestionSetId(UUID questionSetId);
    
    void toggleActiveStatus(UUID id, boolean isActive);
    
    boolean existsByQuestionSetAndQuestion(UUID questionSetId, UUID questionId);
    
    int countActiveItemsInQuestionSet(UUID questionSetId);
}
