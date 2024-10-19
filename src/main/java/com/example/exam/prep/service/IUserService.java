package com.example.exam.prep.service;

import com.example.exam.prep.model.User;
import org.springframework.context.annotation.Bean;

import java.util.List;

public interface IUserService {
    User getUser(Long id);

    List<User> getAllUsers();

    boolean saveUser(User user);

    void deleteUser(Long id);
}