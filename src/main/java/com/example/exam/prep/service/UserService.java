package com.example.exam.prep.service;

import com.example.exam.prep.config.automapper.GenericMapper;
import com.example.exam.prep.model.User;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserService implements IUserService {
    @Autowired
    private IUnitOfWork unitOfWork;

    public UserService() {
    }

    public User getUser(Long id) {
        throw new UnsupportedOperationException();
    }

    public List<User> getAllUsers() {
        List<User> users = unitOfWork.getUserRepository().findAll();
        return users;
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