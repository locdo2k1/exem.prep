package com.example.exam.prep.service;

import com.example.exam.prep.model.User;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LoginService implements ILoginService {
    private final IUnitOfWork unitOfWork;

    private final String SECRET_KEY;

    @Autowired
    public LoginService(@Value("${secret.key}") String secretKey, IUnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
        this.SECRET_KEY = secretKey;
    }

    @Override
    public String login(String username, String password) {
        User user = unitOfWork.getUserRepository().findByUsername(username);
        if (user != null) {
            if (user.getPassword().equals(password)) {
                return generateToken(user);
            }
        }
        throw new RuntimeException("Invalid username or password");
    }

    @Override
    public User validateToken(String token) {
        try {
            byte[] secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            String username = claims.getSubject();
            User user = unitOfWork.getUserRepository().findByUsername(username);
            return user;
        } catch (JwtException e) {
            return null;
        }
    }

    public String generateToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 30 * 60 * 1000); // 30 minutes
        // Create a secure secret key for HS256
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, this.SECRET_KEY)
                .compact();
    }

    @Override
    public User getUserFromToken(String token) {
        return validateToken(token);
    }

    @Override
    public User register(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        unitOfWork.getUserRepository().save(user);
        return user;
    }
}