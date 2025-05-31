package com.example.exam.prep.controller;

import com.example.exam.prep.constant.response.AuthResponseMessage;
import com.example.exam.prep.model.User;
import com.example.exam.prep.model.request.RegisterRequest;
import com.example.exam.prep.model.viewmodels.response.ApiResponse;
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
    public ResponseEntity<ApiResponse<String>> login(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String base64Credentials = authHeader.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
            String[] values = credentials.split(":");
            String username = values[0];
            String password = values[1];

            try {
                String token = authService.login(username, password);
                return ResponseEntity.ok(ApiResponse.success(token, AuthResponseMessage.LOGIN_SUCCESS.getMessage()));
            } catch (InvalidParameterException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(AuthResponseMessage.INVALID_CREDENTIALS.getMessage(), HttpStatus.UNAUTHORIZED.value()));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(AuthResponseMessage.MISSING_AUTH_HEADER.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody RegisterRequest registerRequest) {
        try {
            boolean existingUser = userService.findByUsername(registerRequest.getUsername()) != null;
            if (existingUser) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(AuthResponseMessage.getUserExistsMessage(registerRequest.getUsername()),
                        HttpStatus.BAD_REQUEST.value()));
            }

            User user = authService.register(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword());
            if (user != null) {
                String token = authService.generateToken(user);
                return ResponseEntity.ok(ApiResponse.success(token, AuthResponseMessage.USER_REGISTER_SUCCESS.getMessage()));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(AuthResponseMessage.FAILED_CREATE_USER.getMessage(), HttpStatus.BAD_REQUEST.value()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(AuthResponseMessage.getErrorMessage(e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<String>> getAuthToken(@RequestParam("code") String code, @RequestParam("provider") String provider) {
        try {
            String token = authService.getAuthToken(code, provider);
            return ResponseEntity.ok(ApiResponse.success(token, AuthResponseMessage.TOKEN_GENERATED.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(AuthResponseMessage.getTokenErrorMessage(e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
