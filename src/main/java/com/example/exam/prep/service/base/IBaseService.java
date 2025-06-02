package com.example.exam.prep.service.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IBaseService<T> {
    List<T> findAll();
    Page<T> findAll(Pageable pageable);
    Optional<T> findById(UUID id);
    T save(T entity);
    void deleteById(UUID id);
}
