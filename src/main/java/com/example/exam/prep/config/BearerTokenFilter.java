package com.example.exam.prep.config;

import com.example.exam.prep.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BearerTokenFilter extends OncePerRequestFilter {
    private static final Logger logger4j = LoggerFactory.getLogger(BearerTokenFilter.class);
    private final String secretKey;

    @Autowired
    public BearerTokenFilter(@Value("${secretKey}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
                String username = claims.getBody().getSubject();
                List<String> authoritiesList = (List<String>) claims.getBody().get("authorities");
                if (authoritiesList == null || authoritiesList.isEmpty()) {
                    authoritiesList = Collections.singletonList("DEFAULT_ROLE"); // or any other default role
                }
                List<SimpleGrantedAuthority> authorities = authoritiesList.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                User user = new User(username, null, "email");
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                authentication.setDetails(user);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger4j.debug("SecurityContextHolder after filter chain: {}", SecurityContextHolder.getContext().getAuthentication());
            } catch (JwtException e) {
                // Handle invalid token
                logger4j.error("Error parsing token", e);
            }
        }
        filterChain.doFilter(request, response);
    }
}