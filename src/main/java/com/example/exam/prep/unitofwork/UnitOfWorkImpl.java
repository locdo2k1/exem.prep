package com.example.exam.prep.unitofwork;

import com.example.exam.prep.model.Question;
import com.example.exam.prep.model.User;
import com.example.exam.prep.repository.GenericRepository;
import com.example.exam.prep.repository.GenericRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UnitOfWorkImpl implements IUnitOfWork {

    private final EntityManager entityManager;
    private final GenericRepository<User, Long> userRepository;

    @Autowired
    public UnitOfWorkImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.userRepository = new GenericRepositoryImpl<>(this.entityManager, User.class);
    }

    @Override
    public GenericRepository<User, Long> getUserRepository() {
        Session session = entityManager.unwrap(Session.class);
        // create a CriteriaBuilder instance
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Question> criteriaQuery = criteriaBuilder.createQuery(Question.class);
        Root<Question> root = criteriaQuery.from(Question.class);
        root.fetch("insertedBy", JoinType.LEFT);
        TypedQuery<Question> typedQuery = entityManager.createQuery(criteriaQuery);
        List<Question> questions = typedQuery.getResultList();

        // Access the User entity that was fetched
        for (Question question : questions) {
            User insertedBy = question.getInsertedBy();
            // Do something with the User entity
        }
        return this.userRepository;
    }

    @Override
    public void beginTransaction() {
        entityManager.getTransaction().begin();
    }

    @Override
    public void commitTransaction() {
        entityManager.getTransaction().commit();
    }

    @Override
    public void rollbackTransaction() {
        entityManager.getTransaction().rollback();
    }

    @Override
    public void close() {
        entityManager.close();
    }
}