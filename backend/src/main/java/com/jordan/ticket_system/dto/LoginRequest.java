package com.jordan.ticket_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Credenciales necesarias para iniciar sesión")
public class LoginRequest {
    @Schema(
            description = "Correo electrónico del usuario.",
            example = "admin@test.com"
    )
    @NotBlank
    @Email
    private String email;

    @Schema(
            description = "Contraseña del usuario.",
            example = "123456"
    )
    @NotBlank
    private String password;
}
