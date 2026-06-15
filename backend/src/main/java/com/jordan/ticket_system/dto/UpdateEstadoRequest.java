package com.jordan.ticket_system.dto;

import com.jordan.ticket_system.entity.EstadoTicket;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEstadoRequest {

    @NotNull(message = "El estado es obligatorio")
    private EstadoTicket estado;
}