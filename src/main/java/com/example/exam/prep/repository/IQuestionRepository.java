package com.example.exam.prep.repository;

import com.example.exam.prep.core.filter.Filter;
import com.example.exam.prep.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IQuestionRepository extends GenericRepository<Question>, JpaSpecificationExecutor<Question> {
    @EntityGraph(attributePaths = {"category", "questionType", "fillBlankAnswers", "options", "fileInfos"})
    Optional<Question> findWithDetailsById(UUID id);
    
    default Page<Question> findAll(Filter<Question> filter, Pageable pageable) {
        return findAll(filter.toSpecification(), pageable);
    }
}
