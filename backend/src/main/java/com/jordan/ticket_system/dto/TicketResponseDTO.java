package com.jordan.ticket_system.dto;

import com.jordan.ticket_system.entity.EstadoTicket;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TicketResponseDTO {

    private Long id;
    private String asunto;
    private String descripcion;
    private String emailRemitente;
    private EstadoTicket estado;
    private boolean seen;
    private LocalDateTime createdAt;
}
