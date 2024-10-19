package com.example.exam.prep.repository;

import com.example.exam.prep.model.BaseEntity;
import com.example.exam.prep.model.viewmodels.CriteriaQueryVM;

import java.util.List;

public interface GenericRepository<T extends BaseEntity, ID> {
    T findById(ID id);
    List<T> findAll();
    boolean save(T entity);
    void saveEntityWithoutChange(T entity);
    boolean delete(T entity);
    boolean softDelete(T entity);
    void update(T entity);
    /**
     * Retrieves a CriteriaQueryVM object for the underlying entity class.
     * This allows for adding additional criteria to the query.
     * @return a CriteriaQueryVM object
     */
    CriteriaQueryVM<T> getCriteriaQuery();
}

