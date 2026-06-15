package com.jordan.ticket_system.controller;

import com.jordan.ticket_system.dto.AuthResponse;
import com.jordan.ticket_system.dto.LoginRequest;
import com.jordan.ticket_system.entity.RefreshToken;
import com.jordan.ticket_system.entity.User;
import com.jordan.ticket_system.repository.UserRepository;
import com.jordan.ticket_system.service.impl.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;

//    @PostMapping("/login")
//    public AuthResponse login(@RequestBody LoginRequest request) {
//        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
//
//        Boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
//
//        if(!matches) {
//            throw new RuntimeException("Password incorrecto");
//        }
//
//        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
//
//        return new AuthResponse(token, user.getRole().name());
//    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {

        User user = authService.login(request);

        // Access Token (corta duración)
        String accessToken = authService.createAccessToken(user);

        // Refresh Token (larga duración)
        RefreshToken refreshToken = authService.createRefreshToken(user);

        // Cookie access token
        ResponseCookie accessCookie = ResponseCookie.from("jwt", accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 15) // 15 minutos
                .sameSite("Lax")
                .build();

        // Cookie refresh token
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 60 * 24 * 7) // 7 días
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return new AuthResponse(
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    @GetMapping("/me")
    public AuthResponse me(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow();

        return new AuthResponse(
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

//    @PostMapping("/logout")
//    public void logout(HttpServletResponse response) {
//        ResponseCookie cookie = ResponseCookie.from("jwt", "")
//                .httpOnly(true)
//                .secure(false)
//                .path("/")
//                .maxAge(0)
//                .sameSite("Lax")
//                .build();
//
//        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//    }

    @PostMapping("/refresh")
    public void refresh(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {

        String newAccessToken = authService.refresh(refreshToken);

        ResponseCookie accessCookie = ResponseCookie.from("jwt", newAccessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 15)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }

    @PostMapping("/logout")
    public void logout(
            @CookieValue(
                    value = "refreshToken", required = false)
                    String refreshToken,
                    HttpServletResponse response){

        authService.logout(refreshToken);

        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
