package com.example.exam.prep.repository;

import com.example.exam.prep.model.Part;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPartRepository extends GenericRepository<Part> {
    @Query("SELECT p FROM Part p WHERE p.name = :name")
    Optional<Part> findByName(@Param("name") String name);
    
    @Query("SELECT p FROM Part p WHERE LOWER(p.name) LIKE LOWER(concat('%', :search, '%'))")
    Page<Part> findByNameContainingIgnoreCase(@Param("search") String search, Pageable pageable);
}
