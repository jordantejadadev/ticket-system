package com.jordan.ticket_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "Informació del usuario autenticado.")
public class AuthResponse {
// Token LocalStorage
//    private String token;
//    private String role;

    //CookitHttpOnly
    @Schema(
            description = "Nombre completo.",
            example = "Admin"
    )
    private String name;

    @Schema(
            description = "Correo electrónico",
            example = "admin@test.com"
    )
    private String email;

    @Schema(
            description = "Rol del usuario",
            example = "ADMIN"
    )
    private String role;
}
