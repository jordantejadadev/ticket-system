package com.jordan.ticket_system.service.impl;

import com.jordan.ticket_system.dto.LoginRequest;
import com.jordan.ticket_system.entity.RefreshToken;
import com.jordan.ticket_system.entity.User;

public interface AuthService {
    User login(LoginRequest request);

    RefreshToken createRefreshToken(User user);

    String refresh(String refreshToken);

    void logout(String refreshTokenValue);

    String createAccessToken(User user);
}
