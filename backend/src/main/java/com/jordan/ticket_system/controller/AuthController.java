package com.jordan.ticket_system.controller;

import com.jordan.ticket_system.dto.AuthResponse;
import com.jordan.ticket_system.dto.ErrorResponse;
import com.jordan.ticket_system.dto.LoginRequest;
import com.jordan.ticket_system.entity.RefreshToken;
import com.jordan.ticket_system.entity.User;
import com.jordan.ticket_system.repository.UserRepository;
import com.jordan.ticket_system.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Authentication",
        description = "Endpoints para autenticación y gestión de sesión"
)
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

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica al usuario y genera las cookies JWT y Refresh Token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request,
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

    @Operation(
            summary = "Obtener usuario autenticado",
            description = "Devuelve la información del usuario actualmente autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario autenticado"),
            @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
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

    @Operation(
            summary = "Renovar Access Token",
            description = "Genera un nuevo Access Token utilizando el Refresh Token almacenado en una cookie."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token renovado"),
            @ApiResponse(responseCode = "401", description = "Refresh Token inválido o expirado",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping("/refresh")
    public void refresh(@CookieValue(
            value = "refreshToken",
            required = false
    ) String refreshToken, HttpServletResponse response) {

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

    @Operation(
            summary = "Cerrar Sesión",
            description = "Elimina las cookies JWT y Refresh Token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesión cerrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
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
