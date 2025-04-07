package com.example.exam.prep.service;

import com.example.exam.prep.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IUserService {
    User getUser(Long id);

    List<User> getAllUsers();

    boolean saveUser(User user);

    void deleteUser(Long id);
}