package com.example.exam.prep.unitofwork;

import com.example.exam.prep.model.User;
import com.example.exam.prep.repository.GenericRepository;
import com.example.exam.prep.repository.GenericRepositoryImpl;
import jakarta.persistence.EntityManager;

public interface IUnitOfWork {
    GenericRepository<User, Long> getUserRepository();
    void beginTransaction();
    void commitTransaction();
    void rollbackTransaction();
    void close();
}

