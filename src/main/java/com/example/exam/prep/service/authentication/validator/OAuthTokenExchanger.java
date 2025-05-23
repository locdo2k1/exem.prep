package com.example.exam.prep.service.authentication.validator;

public interface OAuthTokenExchanger {
    String exchangeCodeAndGetEmail(String code);
}