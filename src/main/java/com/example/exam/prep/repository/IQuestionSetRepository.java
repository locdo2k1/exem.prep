package com.example.exam.prep.repository;

import com.example.exam.prep.model.QuestionSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IQuestionSetRepository extends JpaRepository<QuestionSet, UUID> {
    
    Optional<QuestionSet> findByIdAndIsDeletedFalse(UUID id);
    
    List<QuestionSet> findAllByIsDeletedFalse();
    
    boolean existsByTitleAndIsDeletedFalse(String title);
    
    @Modifying
    @Query("UPDATE QuestionSet qs SET qs.isDeleted = true WHERE qs.id = :id")
    void softDelete(@Param("id") UUID id);
    
    @Query("SELECT qs FROM QuestionSet qs JOIN qs.questionSetItems qsi WHERE qsi.question.id = :questionId AND qs.isDeleted = false")
    List<QuestionSet> findByQuestionId(@Param("questionId") UUID questionId);
}
