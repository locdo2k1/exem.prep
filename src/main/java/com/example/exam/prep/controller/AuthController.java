package com.example.exam.prep.controller;

import com.example.exam.prep.model.User;
import com.example.exam.prep.model.request.RegisterRequest;
import com.example.exam.prep.service.IUserService;
import com.example.exam.prep.service.authentication.IAuthService;
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

    private final IAuthService authService;
    private final IUserService userService;

    @Autowired
    public AuthController(IAuthService authService, IUserService userService) {
        this.authService = authService;
        this.userService = userService;
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
                String token = authService.login(username, password);
                return ResponseEntity.ok(token);
            } catch (InvalidParameterException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Authorization header");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        try {
            boolean existingUser = userService.findByUsername(registerRequest.getUsername()) != null;
            if (existingUser) {
                return ResponseEntity.badRequest().body("Username '" + registerRequest.getUsername() + "' already exists");
            }

            User user = authService.register(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword());
            if (user != null) {
                String token = authService.generateToken(user);
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.badRequest().body("Failed to create user");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating user: " + e.getMessage());
        }
    }

    @PostMapping("/token")
    public ResponseEntity<String> getAuthToken(@RequestParam("code") String code, @RequestParam("provider") String provider) {
        String token = authService.getAuthToken(code, provider);
        return ResponseEntity.ok(token);
    }
}
