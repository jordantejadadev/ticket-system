package com.jordan.ticket_system.dto;

import com.jordan.ticket_system.entity.EstadoTicket;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "Información de un ticket devuelta por la API.")
public class TicketResponseDTO {

    @Schema(
            description = "Identificador único del ticket.",
            example = "15"
    )
    private Long id;

    @Schema(
            description = "Asunto del ticket.",
            example = "No puedo iniciar sesión"
    )
    private String asunto;

    @Schema(
            description = "Descripción detallada del problema.",
            example = "Al ingresar mis credenciales aparece un error 500."
    )
    private String descripcion;

    @Schema(
            description = "Correo electrónico del remitente.",
            example = "jordan@test.com"
    )
    private String emailRemitente;

    @Schema(
            description = "Estado actual del ticket."
    )
    private EstadoTicket estado;

    @Schema(
            description = "Indica si el ticket ya fue visualizado por un administrador.",
            example = "false"
    )
    private boolean seen;

    @Schema(
            description = "Fecha y hora de creación del ticket.",
            example = "2026-06-14T14:30:00"
    )
    private LocalDateTime createdAt;
}
