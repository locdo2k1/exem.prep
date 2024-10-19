package com.example.exam.prep.repository;

import com.example.exam.prep.model.BaseEntity;
import com.example.exam.prep.model.User;
import com.example.exam.prep.model.viewmodels.CriteriaQueryVM;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GenericRepositoryImpl<T extends BaseEntity, ID> implements GenericRepository<T, ID> {
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
    public boolean save(T entity) {
        try {
            if (entity.getId() == null) {
                // Add new entity
                entity.setInsertedAt(LocalDateTime.now());
                entity.setInsertedBy(getCurrentUser());
            }
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setUpdatedBy(getCurrentUser());
            entityManager.getTransaction().begin();
            if (entityManager.contains(entity)) {
                entityManager.flush();
            } else {
                entityManager.persist(entity);
            }
            entityManager.getTransaction().commit();
            return true;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            return false;
        }
    }

    @Override
    public void saveEntityWithoutChange(T entity) {
        if (entity.getId() == null) {
            // Add new entity
            entity.setInsertedAt(LocalDateTime.now());
            entity.setInsertedBy(getCurrentUser());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(getCurrentUser());
        entityManager.getTransaction().begin();
        if (entityManager.contains(entity)) {
            entityManager.flush();
        } else {
            entityManager.persist(entity);
        }
    }

    public boolean saveChanges() {
        try {
            entityManager.getTransaction().begin();
            for (T entity : changes) {
                saveEntityWithoutChange(entity);
            }
            entityManager.getTransaction().commit();
            changes.clear();
            return true;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            changes.clear();
            return false;
        }
    }

    @Override
    public boolean delete(T entity) {
        try {
            // Soft delete
            entity.setIsDeleted(true);
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setUpdatedBy(getCurrentUser());
            return save(entity);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean softDelete(T entity) {
        entity.setIsDeleted(true);
        return this.save(entity);
    }

    private final List<T> changes = new ArrayList<>();
    @Override
    public void update(T entity) {
        changes.add(entity);
    }

    @Override
    public CriteriaQueryVM<T> getCriteriaQuery() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root);

        CriteriaQueryVM<T> criteriaQueryVM = new CriteriaQueryVM<>();
        criteriaQueryVM.setCriteriaQuery(cq);
        criteriaQueryVM.setRoot(root);

        return criteriaQueryVM;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                return ((User) principal);
            }
        }
        return null; // or throw an exception if no user is authenticated
    }
}