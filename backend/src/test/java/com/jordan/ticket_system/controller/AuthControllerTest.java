package com.jordan.ticket_system.controller;

import com.jordan.ticket_system.dto.LoginRequest;
import com.jordan.ticket_system.entity.RefreshToken;
import com.jordan.ticket_system.entity.Role;
import com.jordan.ticket_system.entity.User;
import com.jordan.ticket_system.exception.UnauthorizedException;
import com.jordan.ticket_system.repository.UserRepository;
import com.jordan.ticket_system.security.CustomUserDetailsService;
import com.jordan.ticket_system.security.JwtAuthenticationEntryPoint;
import com.jordan.ticket_system.security.JwtService;
import com.jordan.ticket_system.service.AuthService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Test
    void shouldRefreshAccessToken() throws Exception {

        when(authService.refresh("refresh-token"))
                .thenReturn("new-jwt-token");

        mockMvc.perform(post("/auth/refresh")
                .cookie(new Cookie("refreshToken", "refresh-token")))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        HttpHeaders.SET_COOKIE,
                        containsString("jwt=new-jwt-token")
                ));

        verify(authService).refresh("refresh-token");
    }

    @Test
    void shouldLogoutSuccessfully() throws Exception {

        MvcResult result = mockMvc.perform(post("/auth/logout")
                .cookie(new Cookie("refreshToken", "refresh-token")))
                .andExpect(status().isOk())
                .andReturn();

        List<String> setCookies = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE);

        assertThat(setCookies.get(0), containsString("jwt="));
        assertThat(setCookies.get(0), containsString("Max-Age=0"));

        assertThat(setCookies.get(1), containsString("refreshToken="));
        assertThat(setCookies.get(1), containsString("Max-Age=0"));

        verify(authService).logout("refresh-token");
    }

    @Test
    void shouldReturnBadRequestWhenLoginRequestIsInvalid() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("");
        request.setPassword("");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void shouldReturnUnauthorizedWhenRefreshTokenCookieIsMissing() throws Exception {

        when(authService.refresh(null))
                .thenThrow(new UnauthorizedException("Refresh token no encontrado"));

        mockMvc.perform(post("/auth/refresh"))
                .andExpect(status().isUnauthorized());

        verify(authService).refresh(null);
    }

    @Test
    void shouldReturn401WhenCredentialsAreInvalid() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("jordan@test.com");
        request.setPassword("wrong-password");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new UnauthorizedException("Credenciales inválidas"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Credenciales inválidas"));

        verify(authService).login(any(LoginRequest.class));
    }

}
