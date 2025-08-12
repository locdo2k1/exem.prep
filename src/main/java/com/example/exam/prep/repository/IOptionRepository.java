package com.example.exam.prep.repository;

import com.example.exam.prep.model.Option;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public interface IOptionRepository extends GenericRepository<Option> {
    List<Option> findByQuestionId(UUID questionId);
}
