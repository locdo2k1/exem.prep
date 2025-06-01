package com.example.exam.prep.service.base;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IBaseService<T> {
    List<T> findAll();
    Optional<T> findById(UUID id);
    T save(T entity);
    void deleteById(UUID id);
}
