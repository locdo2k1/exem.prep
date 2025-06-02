package com.example.exam.prep.repository;

import com.example.exam.prep.model.QuestionCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IQuestionCategoryRepository extends GenericRepository<QuestionCategory> {
    Optional<QuestionCategory> findByCode(String code);
    
    Page<QuestionCategory> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
        String name, String code, Pageable pageable
    );
    
    Page<QuestionCategory> findAll(Pageable pageable);
}
