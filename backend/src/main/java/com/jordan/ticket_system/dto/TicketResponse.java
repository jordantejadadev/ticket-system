package com.jordan.ticket_system.dto;

import java.time.LocalDateTime;

public class TicketResponse {

    private Long id;
    private String asunto;
    private String emailCliente;
    private String estado;
    private String prioridad;
    private LocalDateTime createdAt;
}