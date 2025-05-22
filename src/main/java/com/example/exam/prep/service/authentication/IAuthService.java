package com.example.exam.prep.service.authentication;

import com.example.exam.prep.model.User;
import org.springframework.http.ResponseEntity;

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

    /**
     * Registers a new user with the given information.
     *
     * @param username the username to register
     * @param email the email to register
     * @param password the password to register
     * @return the registered user
     */
    User register(String username, String email, String password);

    /**
     * Generates a token for the given user.
     *
     * @param user the user to generate a token for
     * @return the token
     */
    String generateToken(User user);

    /**
     * Returns an auth token for the given code and provider.
     *
     * @param code     the authorization code
     * @param provider the authentication provider (e.g. Google, Facebook)
     * @return the auth token, or null if authentication fails
     */
    String getAuthToken(String code, String provider);
}