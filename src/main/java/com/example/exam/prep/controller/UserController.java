package com.example.exam.prep.controller;

import com.example.exam.prep.model.User;
import com.example.exam.prep.model.viewmodels.response.ApiResponse;
import com.example.exam.prep.service.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

import static com.example.exam.prep.constant.response.UserResponseMessage.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users, USERS_RETRIEVED.getMessage()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable UUID id) {
        User user = userService.getUser(id);
        if (user != null) {
            return ResponseEntity.ok(ApiResponse.success(user, USER_RETRIEVED.getMessage()));
        }
        return ResponseEntity.ok(ApiResponse.error(getNotFoundMessage(), 404));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
        boolean isSuccess = userService.saveUser(user);
        if (isSuccess) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(user, USER_CREATED.getMessage()));
        }
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(USER_CREATE_FAILED.getMessage(), 400));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable UUID id, @RequestBody User user) {
        User existingUser = userService.getUser(id);
        if (existingUser == null) {
            return ResponseEntity.ok(ApiResponse.error(getNotFoundMessage(), 404));
        }

        user.setId(id);
        boolean isSuccess = userService.saveUser(user);
        if (isSuccess) {
            return ResponseEntity.ok(ApiResponse.success(user, USER_UPDATED.getMessage()));
        }
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(USER_UPDATE_FAILED.getMessage(), 400));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        User existingUser = userService.getUser(id);
        if (existingUser == null) {
            return ResponseEntity.ok(ApiResponse.error(getNotFoundMessage(), 404));
        }

        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.<Void>success(null, USER_DELETED.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(USER_DELETE_FAILED.getMessage(), 400));
        }
    }
}