package com.example.exam.prep.repository;

import com.example.exam.prep.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class UserRepository extends GenericRepositoryImpl<User, Long> implements IUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public UserRepository(EntityManager entityManager) {
        super(entityManager, User.class);
        this.entityManager = entityManager;
    }

    @Override
    public User findByUsername(String username) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        cq.where(cb.equal(root.get("username"), username));
        return entityManager.createQuery(cq).getResultList().stream().findFirst().orElse(null);
    }
}
