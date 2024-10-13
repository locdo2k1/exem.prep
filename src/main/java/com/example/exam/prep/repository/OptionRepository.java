package com.example.exam.prep.repository;

import com.example.exam.prep.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    // Additional query methods can be defined here
}
