package com.example.exam.prep.repository;

import com.example.exam.prep.model.QuestionSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IQuestionSetRepository extends JpaRepository<QuestionSet, UUID> {
    
    @Override
    @EntityGraph(attributePaths = {
        "questionSetItems",
        "questionSetItems.question",
        "questionSetItems.question.options",
        "questionSetItems.question.fillBlankAnswers"
    })
    Optional<QuestionSet> findById(UUID id);
    
    @EntityGraph(attributePaths = {
        "questionSetItems",
        "questionSetItems.question",
        "questionSetItems.question.options",
        "questionSetItems.question.fillBlankAnswers"
    })
    @Query("SELECT qs FROM QuestionSet qs WHERE qs.id = :id AND qs.isDeleted = false")
    Optional<QuestionSet> findByIdAndIsDeletedFalse(@Param("id") UUID id);
    
    @EntityGraph(attributePaths = {
        "questionSetItems",
        "questionSetItems.question",
        "questionSetItems.question.options",
        "questionSetItems.question.fillBlankAnswers"
    })
    @Query("SELECT qs FROM QuestionSet qs WHERE qs.isDeleted = false")
    Page<QuestionSet> findAllByIsDeletedFalse(Pageable pageable);
    
    boolean existsByTitleAndIsDeletedFalse(String title);
    
    @Modifying
    @Query("UPDATE QuestionSet qs SET qs.isDeleted = true WHERE qs.id = :id")
    void softDelete(@Param("id") UUID id);
    
    @Query("SELECT qs FROM QuestionSet qs JOIN qs.questionSetItems qsi WHERE qsi.question.id = :questionId AND qs.isDeleted = false")
    List<QuestionSet> findByQuestionId(@Param("questionId") UUID questionId);
    
    @EntityGraph(attributePaths = {
        "questionSetItems",
        "questionSetItems.question",
        "questionSetItems.question.options",
        "questionSetItems.question.fillBlankAnswers"
    })
    @Query("SELECT qs FROM QuestionSet qs WHERE LOWER(qs.title) LIKE LOWER(CONCAT('%', :title, '%')) AND qs.isDeleted = false")
    Page<QuestionSet> findAllByTitleContaining(@Param("title") String title, Pageable pageable);
}
