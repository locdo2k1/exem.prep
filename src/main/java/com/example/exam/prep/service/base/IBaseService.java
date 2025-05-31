package com.example.exam.prep.service.base;

import java.util.List;
import java.util.Optional;

public interface IBaseService<T> {
    List<T> findAll();
    Optional<T> findById(Long id);
    T save(T entity);
    void deleteById(Long id);
}
