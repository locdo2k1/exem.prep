package com.example.exam.prep.repository;
import com.example.exam.prep.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    // Additional query methods can be defined here
}


