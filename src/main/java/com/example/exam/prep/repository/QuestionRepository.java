package com.example.exam.prep.repository;

import com.example.exam.prep.core.filter.Filter;
import com.example.exam.prep.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID>, JpaSpecificationExecutor<Question> {
    default Page<Question> findAll(Filter<Question> filter, Pageable pageable) {
        return findAll(filter.toSpecification(), pageable);
    }
}
