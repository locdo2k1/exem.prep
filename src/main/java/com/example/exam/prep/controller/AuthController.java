package com.example.exam.prep.controller;

import com.example.exam.prep.model.User;
import com.example.exam.prep.model.request.LoginRequest;
import com.example.exam.prep.model.request.RegisterRequest;
import com.example.exam.prep.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginService loginService;

    @Autowired
    public AuthController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        String token = loginService.login(loginRequest.getUsername(), loginRequest.getPassword());

        return token;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest registerRequest) {
        User user = loginService.register(registerRequest.getUsername(), registerRequest.getPassword());
        if (user != null) {
            return loginService.generateToken(user);
        } else {
            return null;
        }
    }
}
