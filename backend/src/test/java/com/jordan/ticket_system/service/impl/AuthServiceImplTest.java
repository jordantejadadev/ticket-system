package com.jordan.ticket_system.service.impl;

import com.jordan.ticket_system.dto.LoginRequest;
import com.jordan.ticket_system.entity.RefreshToken;
import com.jordan.ticket_system.entity.Role;
import com.jordan.ticket_system.entity.User;
import com.jordan.ticket_system.exception.UnauthorizedException;
import com.jordan.ticket_system.repository.RefreshTokenRepository;
import com.jordan.ticket_system.repository.UserRepository;
import com.jordan.ticket_system.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldLoginSuccessfully() {

        LoginRequest request = new LoginRequest();
        request.setEmail("jordan@test.com");
        request.setPassword("123456");

        User user = new User();
        user.setEmail("jordan@test.com");
        user.setPassword("passwordEncriptado");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )).thenReturn(true);

        User result = authService.login(request);

        assertNotNull(result);

        assertEquals("jordan@test.com", result.getEmail());

        verify(userRepository).findByEmail(request.getEmail());

        verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        LoginRequest request = new LoginRequest();
        request.setEmail("jordan@test.com");
        request.setPassword("123456");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        assertThrows(
                UnauthorizedException.class,
                () -> authService.login(request)
        );

        verify(userRepository).findByEmail(request.getEmail());

        verify(passwordEncoder, never())
                .matches(any(), any());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {

        LoginRequest request = new LoginRequest();
        request.setEmail("jordan@test.com");
        request.setPassword("passwordIncorrecto");

        User user = new User();
        user.setEmail("jordan@test.com");
        user.setPassword("passwordEncriptado");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )).thenReturn(false);

        assertThrows(
                UnauthorizedException.class,
                () -> authService.login(request)
        );

        verify(userRepository).findByEmail(request.getEmail());

        verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
    }

    @Test
    void shouldCreateRefreshToken() {

        User user = new User();
        user.setId(1L);
        user.setEmail("jordan@test.com");

        RefreshToken savedToken = RefreshToken.builder()
                .token("refresh-token")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .user(user)
                .build();

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenReturn(savedToken);

        RefreshToken result = authService.createRefreshToken(user);

        assertNotNull(result);
        assertEquals("refresh-token", result.getToken());

        verify(refreshTokenRepository).deleteByUser(user);

        verify(refreshTokenRepository)
                .save(any(RefreshToken.class));

    }

    @Test
    void shouldGenerateNewAccessTokenWhenRefreshTokenIsValid() {

        User user = new User();
        user.setEmail("jordan@test.com");
        user.setRole(Role.ADMIN);

        RefreshToken refreshToken = RefreshToken.builder()
                .token("refresh-token")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .user(user)
                .build();

        when(refreshTokenRepository.findByToken("refresh-token"))
                .thenReturn(Optional.of(refreshToken));

        when(jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        )).thenReturn("new-jwt-token");

        String result = authService.refresh("refresh-token");

        assertEquals("new-jwt-token", result);

        verify(refreshTokenRepository)
                .findByToken("refresh-token");

        verify(jwtService).generateToken(
                user.getEmail(),
                user.getRole().name()
        );
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenIsNull() {

        assertThrows(
                UnauthorizedException.class,
                () -> authService.refresh(null)
        );

        verify(refreshTokenRepository, never()).findByToken(any());

        verify(jwtService, never())
                .generateToken(any(), any());
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenIsInvalid() {

        when(refreshTokenRepository.findByToken("invalid-token"))
                .thenReturn(Optional.empty());

        assertThrows(
                UnauthorizedException.class,
                () -> authService.refresh("invalid-token")
        );

        verify(refreshTokenRepository)
                .findByToken("invalid-token");

        verify(jwtService, never())
                .generateToken(any(), any());
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenIsExpired() {

        User user = new User();
        user.setEmail("jordan@test.com");

        RefreshToken refreshToken = RefreshToken.builder()
                .token("expired-token")
                .expiresAt(LocalDateTime.now().minusDays(1))
                .user(user)
                .build();

        when(refreshTokenRepository.findByToken("expired-token"))
                .thenReturn(Optional.of(refreshToken));

        assertThrows(
                UnauthorizedException.class,
                () -> authService.refresh("expired-token")
        );

        verify(refreshTokenRepository)
                .findByToken("expired-token");

        verify(refreshTokenRepository)
                .delete(refreshToken);

        verify(jwtService, never())
                .generateToken(any(), any());
    }

    @Test
    void shouldDeleteRefreshTokenOnLogout() {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        when(refreshTokenRepository.findByToken("refresh-token"))
                .thenReturn(Optional.of(refreshToken));

        authService.logout("refresh-token");

        verify(refreshTokenRepository)
                .findByToken("refresh-token");

        verify(refreshTokenRepository)
                .delete(refreshToken);
    }

    @Test
    void shouldDoNothingWhenRefreshTokenIsNull() {

        authService.logout(null);

        verifyNoInteractions(refreshTokenRepository);
    }

    @Test
    void shouldGenerateAccessToken() {
        User user = new User();
        user.setEmail("jordan@test.com");
        user.setRole(Role.ADMIN);

        when(jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        )).thenReturn("jwt-token");

        String result = authService.createAccessToken(user);

        assertEquals("jwt-token", result);

        verify(jwtService)
                .generateToken(
                        user.getEmail(),
                        user.getRole().name()
                );
    }


}
