package com.example.exam.prep.repository;

import com.example.exam.prep.model.Question;
import com.example.exam.prep.model.QuestionSet;
import com.example.exam.prep.model.QuestionSetItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IQuestionSetItemRepository extends JpaRepository<QuestionSetItem, UUID> {
    
    Optional<QuestionSetItem> findByIdAndQuestionSetIsDeletedFalse(UUID id);
    
    List<QuestionSetItem> findByQuestionSetIdAndQuestionSetIsDeletedFalse(UUID questionSetId);
    
    List<QuestionSetItem> findByQuestionSetIdAndIsActiveTrueAndQuestionSetIsDeletedFalse(UUID questionSetId);
    
    boolean existsByQuestionSetAndQuestion(QuestionSet questionSet, Question question);
    
    @Modifying
    @Query("UPDATE QuestionSetItem qsi SET qsi.isActive = :isActive WHERE qsi.id = :id")
    void updateActiveStatus(@Param("id") UUID id, @Param("isActive") boolean isActive);
    
    @Query("SELECT qsi FROM QuestionSetItem qsi WHERE qsi.questionSet.id = :questionSetId AND qsi.question.id = :questionId")
    Optional<QuestionSetItem> findByQuestionSetIdAndQuestionId(
            @Param("questionSetId") UUID questionSetId, 
            @Param("questionId") UUID questionId);
    
    @Query("SELECT COUNT(qsi) FROM QuestionSetItem qsi WHERE qsi.questionSet.id = :questionSetId AND qsi.isActive = true")
    long countActiveItemsByQuestionSetId(@Param("questionSetId") UUID questionSetId);
}
