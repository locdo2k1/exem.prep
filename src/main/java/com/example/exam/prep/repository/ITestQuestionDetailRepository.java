package com.example.exam.prep.repository;

import com.example.exam.prep.model.TestQuestionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ITestQuestionDetailRepository extends JpaRepository<TestQuestionDetail, UUID> {
}
