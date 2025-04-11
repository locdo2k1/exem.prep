package com.example.exam.prep.service;

import com.example.exam.prep.model.User;

public interface IAuthService {
    /**
     * Authenticates a user and returns a token if successful.
     *
     * @param username the username to authenticate
     * @param password the password to authenticate
     * @return a token if authentication is successful, or null if not
     * @throws RuntimeException if authentication fails
     */
    String login(String username, String password);

    /**
     * Validates a token and returns the corresponding user if valid.
     *
     * @param token the token to validate
     * @return the user associated with the token, or null if invalid
     */
    User validateToken(String token);

    /**
     * Returns the user associated with the given token.
     *
     * @param token the token to retrieve the user for
     * @return the user associated with the token, or null if not found
     */
    User getUserFromToken(String token);

    User register(String username, String email, String password);
}