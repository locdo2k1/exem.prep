package com.example.exam.prep.service;

import com.example.exam.prep.model.User;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService implements IUserService {
    private final IUnitOfWork unitOfWork;

    public UserService(IUnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public User getUser(Long id) {
        throw new UnsupportedOperationException();
    }

    public List<User> getAllUsers() {
        return unitOfWork.getUserRepository().findAll();
    }

    public boolean saveUser(User user) {
        throw new UnsupportedOperationException();
    }

    public void deleteUser(Long id) {
        throw new UnsupportedOperationException();
    }

    public boolean register(String username, String password) {
        throw new UnsupportedOperationException();
    }
}