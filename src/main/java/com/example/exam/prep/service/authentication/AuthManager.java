package com.example.exam.prep.service.authentication;

import com.example.exam.prep.service.authentication.validator.OAuthTokenExchanger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthManager {
    private final List<OAuthTokenExchanger> validators;

    @Autowired
    public AuthManager(List<OAuthTokenExchanger> validators) {
        this.validators = validators;
    }

    public String getEmail(String code, String provider) {
        for (OAuthTokenExchanger exchanger : validators) {
            if (provider.equalsIgnoreCase("google")) {
                return exchanger.exchangeCodeAndGetEmail(code);
            }
//            else if (provider.equalsIgnoreCase("facebook") && exchanger instanceof FacebookOAuthTokenExchanger) {
//                return ((FacebookOAuthTokenExchanger) exchanger).exchangeCodeAndGetEmail(code);
//            }
        }
        return null;
    }
}