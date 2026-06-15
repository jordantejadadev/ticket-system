package com.jordan.ticket_system.service.impl;

import com.jordan.ticket_system.dto.LoginRequest;
import com.jordan.ticket_system.entity.RefreshToken;
import com.jordan.ticket_system.entity.User;
import com.jordan.ticket_system.repository.RefreshTokenRepository;
import com.jordan.ticket_system.repository.UserRepository;
import com.jordan.ticket_system.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public User login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Password incorrecto");
        }

        return user;
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {

        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .user(user)
                .build();

        RefreshToken saved = refreshTokenRepository.save(refreshToken);

        return saved;
    }

    @Override
    public String refresh(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Refresh token inválido"));

        if(refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expirado");
        }

        User user = refreshToken.getUser();

        return jwtService.generateToken(user.getEmail(), user.getRole().name());
    }

    @Override
    public void logout(String refreshTokenValue) {

        if(refreshTokenValue == null) {
            return;
        }

        refreshTokenRepository.findByToken(refreshTokenValue).ifPresent(refreshTokenRepository::delete);

    }

    @Override
    public String createAccessToken(User user) {

        return jwtService.generateToken(user.getEmail(), user.getRole().name());
    }

}
