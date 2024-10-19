package com.example.exam.prep.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.hibernate.Session;

public class GenericRepositoryImpl<T, ID> implements GenericRepository<T, ID> {
    protected EntityManager entityManager;
    protected Class<T> entityClass;

    public GenericRepositoryImpl(EntityManager entityManager, Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    @Override
    public T findById(ID id) {
        return entityManager.find(entityClass, id);
    }

    @Override
    public List<T> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root);

        return entityManager.createQuery(cq).getResultList();

    }

    @Override
    public T save(T entity) {
        entityManager.getTransaction().begin();
        if (entityManager.contains(entity)) {
            entityManager.flush();
        } else {
            entityManager.persist(entity);
        }
        entityManager.getTransaction().commit();
        return entity;
    }

    @Override
    public void delete(T entity) {
        entityManager.remove(entity);
    }
}