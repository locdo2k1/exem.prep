package com.example.exam.prep.service.impl;

import com.example.exam.prep.model.Question;
import com.example.exam.prep.model.QuestionSet;
import com.example.exam.prep.model.QuestionSetItem;
import com.example.exam.prep.model.viewmodels.questionset.QuestionSetCreateVM;
import com.example.exam.prep.service.IQuestionSetService;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import com.example.exam.prep.model.viewmodels.questionset.QuestionSetVM;
import java.util.Set;
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
        // Using a custom query to find by title containing (case-insensitive)
        return unitOfWork.getQuestionSetRepository().findAllByTitleContaining(title, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionSetVM> findAllQuestionSetVMs(Pageable pageable) {
        Page<QuestionSet> questionSets = unitOfWork.getQuestionSetRepository().findAllByIsDeletedFalse(pageable);
        return questionSets.map(questionSet -> {
            QuestionSetVM vm = new QuestionSetVM();
            vm.setId(questionSet.getId());
            vm.setTitle(questionSet.getTitle());
            vm.setDescription(questionSet.getDescription());
            vm.setImageUrl(questionSet.getImageUrl());
            vm.setOrder(questionSet.getOrder());
            // The questions list will be populated by the fromEntity method
            return QuestionSetVM.fromEntity(questionSet);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionSetVM> findQuestionSetVMsByTitleContaining(String title, Pageable pageable) {
        Page<QuestionSet> questionSets = unitOfWork.getQuestionSetRepository().findAllByTitleContaining(title, pageable);
        return questionSets.map(questionSet -> {
            QuestionSetVM vm = new QuestionSetVM();
            vm.setId(questionSet.getId());
            vm.setTitle(questionSet.getTitle());
            vm.setDescription(questionSet.getDescription());
            vm.setImageUrl(questionSet.getImageUrl());
            vm.setOrder(questionSet.getOrder());
            // The questions list will be populated by the fromEntity method
            return QuestionSetVM.fromEntity(questionSet);
        });
    }

    @Override
    @Transactional
    public QuestionSet createQuestionSet(QuestionSetCreateVM questionSetVM) {
        // Create new QuestionSet from VM
        QuestionSet questionSet = new QuestionSet();
        questionSet.setTitle(questionSetVM.getTitle());
        questionSet.setDescription(questionSetVM.getDescription());
        questionSet.setOrder(questionSetVM.getOrder());
        
        // Handle file uploads if any
        if (questionSetVM.getAudioFiles() != null && !questionSetVM.getAudioFiles().isEmpty()) {
            // Add your file handling logic here
            // For example: process and save files, then set file paths to the questionSet
        }
        
        // Save the question set first to generate an ID
        QuestionSet savedQuestionSet = unitOfWork.getQuestionSetRepository().save(questionSet);
        
        // Handle question set items if any
        if (questionSetVM.getQuestionIds() != null && !questionSetVM.getQuestionIds().isEmpty()) {
            Set<QuestionSetItem> questionSetItems = new HashSet<>();
            // Convert questionIds to a list for iteration
            List<UUID> questionIds = questionSetVM.getQuestionIdsAsList();
            for (UUID questionId : questionIds) {
                Question question = unitOfWork.getQuestionRepository().findById(questionId)
                        .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + questionId));
                
                QuestionSetItem item = new QuestionSetItem();
                item.setQuestion(question);
                item.setQuestionSet(savedQuestionSet);
                item.setOrder(questionSetItems.size() + 1); // Set order based on position in the list
                questionSetItems.add(item);
            }
            savedQuestionSet.setQuestionSetItems(questionSetItems);
            
            // Update to save the relationships
            return unitOfWork.getQuestionSetRepository().save(savedQuestionSet);
        }
        
        return savedQuestionSet;
    }
}
