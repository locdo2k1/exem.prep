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
        User user = unitOfWork.getUserRepository().findById(id);
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = unitOfWork.getUserRepository().findAll();
        return users;
    }

    public boolean saveUser(User user) {
        User findedUser = unitOfWork.getUserRepository().findById(user.getId());
        GenericMapper.toEntity(user, findedUser);
        return unitOfWork.getUserRepository().save(findedUser);
    }

    public void deleteUser(Long id) {
        User user = unitOfWork.getUserRepository().findById(id);
        if (user != null) {
            unitOfWork.getUserRepository().delete(user);
        }
    }

    public boolean register(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return unitOfWork.getUserRepository().save(user);
    }
}