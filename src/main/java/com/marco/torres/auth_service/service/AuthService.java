package com.marco.torres.auth_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.marco.torres.auth_service.dto.AuthResponse;
import com.marco.torres.auth_service.dto.LoginRequest;
import com.marco.torres.auth_service.dto.RefreshRequest;
import com.marco.torres.auth_service.entity.RefreshToken;
import com.marco.torres.auth_service.entity.User;
import com.marco.torres.auth_service.repository.RefreshTokenRepository;
import com.marco.torres.auth_service.repository.UserRepository;
import com.marco.torres.auth_service.security.CustomUserDetails;
import com.marco.torres.auth_service.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${jwt.refresh-expiration-ms}")
    private Long refreshExpirationMs;

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse login(LoginRequest request) {
        AuthenticationManager authenticationManager = null;
        Authentication authentication = null;
        try {
            authenticationManager = authenticationConfiguration.getAuthenticationManager();
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getEmail())
                    .orElseThrow();

            String accesToken = jwtService.generateToken(user);
            String refreshTokenValue = jwtService.generateRefreshToken(user.getEmail());

            RefreshToken refreshToken = refreshTokenService.create(user.getEmail(), refreshTokenValue);
            refreshTokenRepository.save(refreshToken);

            return new AuthResponse(accesToken, refreshTokenValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AuthResponse refreshToken(RefreshRequest request) {
        RefreshToken storedToken = refreshTokenService.validate(request.getRefreshToken());

        String email = storedToken.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow();

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(email);

        refreshTokenService.rotate(storedToken, newRefreshToken);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}
