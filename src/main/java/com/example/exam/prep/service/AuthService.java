package com.example.exam.prep.service;

import com.example.exam.prep.model.User;
import com.example.exam.prep.model.viewmodels.UserInfoVM;
import com.example.exam.prep.unitofwork.IUnitOfWork;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidParameterException;
import java.util.Date;

@Service
public class AuthService implements IAuthService {
    private final IUnitOfWork unitOfWork;
    private final String secretKey;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(@Value("${secret.key}") String secretKey, IUnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
        this.secretKey = secretKey;
    }

    @Override
    public String login(String username, String password) {
        User user = unitOfWork.getUserRepository().findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return generateToken(user);
        }
        throw new InvalidParameterException("Invalid username or password");
    }

    @Override
    public User validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                                .setSigningKey(new SecretKeySpec(this.secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName()))
                                .build().parseClaimsJws(token).getBody();
            String username = claims.getSubject();
            return unitOfWork.getUserRepository().findByUsername(username);
        } catch (JwtException e) {
            return null;
        }
    }

    public String generateToken(User user) {
        Claims claims = Jwts.claims()
                            .setSubject(user.getUsername());
        UserInfoVM userInfoVM = new UserInfoVM();
        userInfoVM.setUsername(user.getUsername());
        userInfoVM.setEmail(user.getEmail());
        claims.put("userInfo", userInfoVM);
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 30 * 60 * 1000); // 30 minutes
        // Create a secure secret key for HS256
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(new SecretKeySpec(this.secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName()))
                .compact();
    }

    @Override
    public User getUserFromToken(String token) {
        return validateToken(token);
    }

    @Override
    public User register(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Hash the password
        return unitOfWork.getUserRepository().save(user);
    }
}