package com.example.exam.prep.service.base;

import com.example.exam.prep.model.BaseEntity;
import com.example.exam.prep.repository.GenericRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
public abstract class BaseService<T extends BaseEntity> implements IBaseService<T> {
    protected final GenericRepository<T> repository;

    protected BaseService(GenericRepository<T> repository) {
        this.repository = repository;
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }


    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Optional<T> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }
    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}

