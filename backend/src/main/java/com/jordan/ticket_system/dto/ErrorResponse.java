package com.jordan.ticket_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Schema(description = "Respuesta de error")
public class ErrorResponse {

    @Schema(example = "400")
    private int status;

    @Schema(example = "Errores de validación")
    private String message;

    @Schema(example = "2026-06-16T14:30:15")
    private LocalDateTime timestamp;

    @Schema(
            description = "Errores de validación por campo"
    )
    private Map<String, String> errors;

    // Constructor normal
    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    // constructor con validaciones
    public ErrorResponse(int status, String message, LocalDateTime timestamp, Map<String, String> errors) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.errors = errors;
    }
}
