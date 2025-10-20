package com.example.exam.prep.util;

import com.example.exam.prep.model.User;
import com.example.exam.prep.repository.IUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Helper class for authentication-related operations
 */
@Component
public class AuthHelper {
    
    private final IUserRepository userRepository;
    
    public AuthHelper(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Gets the authenticated user's ID
     * @return UUID of the authenticated user, or null if not authenticated or user not found
     */
    public UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User userDetails = 
                (User) authentication.getPrincipal();
            try {
                User user = userRepository.findByUsername(userDetails.getUsername());
                return user != null ? user.getId() : null;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
    
    /**
     * Gets the authenticated user's ID or throws an exception if not authenticated
     * @return UUID of the authenticated user
     * @throws IllegalStateException if user is not authenticated or user not found
     */
    public UUID getAuthenticatedUserIdOrThrow() {
        UUID userId = getAuthenticatedUserId();
        if (userId == null) {
            throw new IllegalStateException("User is not authenticated or user not found");
        }
        return userId;
    }
}
