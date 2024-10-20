package com.example.exam.prep.repository;

import com.example.exam.prep.model.User;

public interface IUserRepository extends GenericRepository<User, Long> {
    User findByUsername(String username);
}

