package com.example.exam.prep.repository;

import com.example.exam.prep.model.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@NoRepositoryBean
public interface GenericRepository<T extends BaseEntity> extends JpaRepository<T, UUID> {
    default void softDelete(T entity) {
        T existingEntity = findById(entity.getId()).orElse(null);
        if (existingEntity != null) {
            save(existingEntity);
        }
    }
}

