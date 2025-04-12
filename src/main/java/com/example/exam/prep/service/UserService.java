package com.example.exam.prep.service;

import com.example.exam.prep.model.User;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {
    private final IUnitOfWork unitOfWork;

    public UserService(IUnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public User getUser(Long id) {
        return unitOfWork.getUserRepository().findById(id).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return unitOfWork.getUserRepository().findAll();
    }

    @Override
    public boolean saveUser(User user) {
        try {
            unitOfWork.getUserRepository().save(user);
            return true;
        } catch (Exception e) {
            // Handle the exception, for example, log the error
            return false;
        }
    }

    @Override
    public void deleteUser(Long id) throws Exception {
        unitOfWork.getUserRepository().deleteById(id);
    }

    @Override
    public User findByUsername(String username) {
        return unitOfWork.getUserRepository().findByUsername(username);
    }
}