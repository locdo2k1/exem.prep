package com.example.exam.prep.service.base;

import com.example.exam.prep.model.BaseEntity;
import com.example.exam.prep.repository.GenericRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

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
    public Optional<T> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}

