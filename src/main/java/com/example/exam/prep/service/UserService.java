package com.example.exam.prep.service;

import com.example.exam.prep.model.User;
import com.example.exam.prep.model.viewmodels.BasicUserInfo;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService implements IUserService {
    private final IUnitOfWork unitOfWork;

    public UserService(IUnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public User getUser(UUID id) {
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
    public void deleteUser(UUID id) {
        unitOfWork.getUserRepository().deleteById(id);
    }

    @Override
    public User findByUsername(String username) {
        return unitOfWork.getUserRepository().findByUsername(username);
    }

    @Override
    public BasicUserInfo getBasicUserInfo(UUID id) {
        User user = getUser(id);
        if (user == null) {
            return null;
        }
        return new BasicUserInfo(
            user.getId(),
            user.getUsername(),
            user.getEmail()
        );
    }
}