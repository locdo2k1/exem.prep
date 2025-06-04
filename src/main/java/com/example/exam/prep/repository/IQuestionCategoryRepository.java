package com.example.exam.prep.repository;

import com.example.exam.prep.model.QuestionCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IQuestionCategoryRepository extends GenericRepository<QuestionCategory> {
    Optional<QuestionCategory> findByCode(String code);
    
    @Query("SELECT q FROM QuestionCategory q WHERE LOWER(q.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<QuestionCategory> findByNameContainingIgnoreCase(
        @Param("name") String name,
        Pageable pageable
    );
    
    @Query("SELECT q FROM QuestionCategory q WHERE LOWER(q.code) LIKE LOWER(CONCAT('%', :code, '%'))")
    Page<QuestionCategory> findByCodeContainingIgnoreCase(
        @Param("code") String code,
        Pageable pageable
    );
    
    Page<QuestionCategory> findAll(Pageable pageable);
}
