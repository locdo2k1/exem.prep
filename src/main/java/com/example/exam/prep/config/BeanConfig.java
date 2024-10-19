package com.example.exam.prep.config;

import com.example.exam.prep.service.IUserService;
import com.example.exam.prep.service.UserService;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import com.example.exam.prep.unitofwork.UnitOfWorkImpl;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Bean
    public IUserService userService() {
        return new UserService();
    }
    @Bean
    public IUnitOfWork unitOfWork() {
        return new UnitOfWorkImpl(entityManagerFactory.createEntityManager());
    }
}
