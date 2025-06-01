package com.example.exam.prep.service;

import com.example.exam.prep.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface IUserService {
    User getUser(UUID id);

    List<User> getAllUsers();

    boolean saveUser(User user);

    void deleteUser(UUID id);

    User findByUsername(String username);
}

