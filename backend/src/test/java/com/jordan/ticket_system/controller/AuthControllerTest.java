package com.jordan.ticket_system.controller;

import com.jordan.ticket_system.dto.LoginRequest;
import com.jordan.ticket_system.entity.RefreshToken;
import com.jordan.ticket_system.entity.Role;
import com.jordan.ticket_system.entity.User;
import com.jordan.ticket_system.repository.UserRepository;
import com.jordan.ticket_system.security.CustomUserDetailsService;
import com.jordan.ticket_system.security.JwtAuthenticationEntryPoint;
import com.jordan.ticket_system.security.JwtFilter;
import com.jordan.ticket_system.security.JwtService;
import com.jordan.ticket_system.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AuthService authService;

    @MockitoBean
    UserRepository userRepository;

    @MockitoBean
    JwtAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    JwtService jwtService;

    @MockitoBean
    CustomUserDetailsService userDetailsService;

    @Test
    void shouldLoginSuccessfully() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("jordan@test.com");
        request.setPassword("123456");

        User user = new User();
        user.setName("Jordan");
        user.setEmail("jordan@test.com");
        user.setRole(Role.ADMIN);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(user);

        when(authService.createAccessToken(user))
                .thenReturn("jwt-token");

        when(authService.createRefreshToken(user))
                .thenReturn(refreshToken);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jordan"))
                .andExpect(jsonPath("$.email").value("jordan@test.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

}
