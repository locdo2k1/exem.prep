package com.example.exam.prep.unitofwork;

import com.example.exam.prep.repository.IUserRepository;

public interface IUnitOfWork {
    IUserRepository getUserRepository();
}

