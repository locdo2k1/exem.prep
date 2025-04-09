package com.example.exam.prep.controller;

import com.example.exam.prep.model.User;
import com.example.exam.prep.model.request.RegisterRequest;
import com.example.exam.prep.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.Base64;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginService loginService;

    @Autowired
    public AuthController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String base64Credentials = authHeader.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
            String[] values = credentials.split(":");
            String username = values[0];
            String password = values[1];

            try {
                String token = loginService.login(username, password);
                return ResponseEntity.ok(token);
            } catch (InvalidParameterException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Authorization header");
        }
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
