package com.example.exam.prep.config;

import com.example.exam.prep.model.User;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {
    @Autowired
    private IUnitOfWork unitOfWork;

    @Bean
    public AuditorAware<User> auditorAware() {
        return new AuditorAware<User>() {
            @Override
            public Optional<User> getCurrentAuditor() {
                // Get the current user from the security context
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getDetails() instanceof String) {
                    String username = authentication.getPrincipal().toString();
                    User user = unitOfWork.getUserRepository().findByUsername(username);
                    return Optional.ofNullable(user);
                }
                return Optional.empty();
            }
        };
    }
}
