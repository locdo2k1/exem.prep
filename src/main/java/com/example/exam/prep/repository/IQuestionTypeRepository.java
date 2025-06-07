package com.example.exam.prep.repository;

import com.example.exam.prep.model.QuestionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IQuestionTypeRepository extends GenericRepository<QuestionType> {
    @Query("SELECT q FROM QuestionType q WHERE LOWER(q.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<QuestionType> findByNameContainingIgnoreCase(
        @Param("name") String name,
        Pageable pageable
    );
}
