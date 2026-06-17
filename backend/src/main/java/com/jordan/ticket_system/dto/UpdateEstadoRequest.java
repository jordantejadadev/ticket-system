package com.jordan.ticket_system.dto;

import com.jordan.ticket_system.entity.EstadoTicket;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(
        description = "Solicitud para actualizar el estado de un ticket."
)
public class UpdateEstadoRequest {

    @Schema(
            description = "Nuevo estado del ticket.",
            example = "ABIERTO",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "El estado es obligatorio")
    private EstadoTicket estado;
}