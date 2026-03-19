package com.marco.torres.auth_service.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.marco.torres.auth_service.entity.RefreshToken;
import com.marco.torres.auth_service.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Value("${jwt.refresh-expiration-ms}")
    private Long refreshExpirationMs;

    public RefreshToken validate(String token) {

        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }

    public void rotate(RefreshToken oldToken, String newTokenValue) {
        oldToken.setRevoked(true);
        repository.save(oldToken);
        create(oldToken.getUsername(), newTokenValue);
    }

    @NonNull
    protected RefreshToken create(String username, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername(username);
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(
                Instant.now().plusMillis(refreshExpirationMs));

        return repository.save(refreshToken);
    }
}