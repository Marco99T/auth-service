package com.marco.torres.auth_service.security;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Date;
import org.springframework.stereotype.Service;

import com.marco.torres.auth_service.entity.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {

    private final RSAPrivateKey privateKey;
    private static final long ACCES_TOKEN = 1000 * 60 * 5; // 15 M
    private static final long REFRESH_TOKEN = 1000 * 60 * 60 * 24 * 7; // 7 D

    public JwtService(RSAPrivateKey rsaKeyConfig) {
        this.privateKey = rsaKeyConfig;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();

        return Jwts.builder()
                .claim("userId", user.getId())
                .claim("role", user.getRole())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ACCES_TOKEN)))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

}
