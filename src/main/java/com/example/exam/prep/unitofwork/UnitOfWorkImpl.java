package com.example.exam.prep.unitofwork;

import com.example.exam.prep.repository.IUserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

@Service
public class UnitOfWorkImpl implements IUnitOfWork {

    private final EntityManager entityManager;

    @Autowired
    private IUserRepository userRepository;

    public UnitOfWorkImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public IUserRepository getUserRepository() {
        return this.userRepository;
    }
}