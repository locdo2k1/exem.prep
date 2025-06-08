package com.example.exam.prep.repository;

import com.example.exam.prep.model.Question;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IQuestionRepository extends GenericRepository<Question> {
    @EntityGraph(attributePaths = {"category", "questionType", "fillBlankAnswers", "options", "fileInfos"})
    Optional<Question> findWithDetailsById(UUID id);
}
