package com.example.exam.prep.repository;

import com.example.exam.prep.model.TestFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ITestFileRepository extends JpaRepository<TestFile, UUID> {
}
