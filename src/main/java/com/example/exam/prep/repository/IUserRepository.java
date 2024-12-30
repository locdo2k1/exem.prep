package com.example.exam.prep.repository;

import com.example.exam.prep.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends GenericRepository<User> {
    User findByUsername(String username);
}

