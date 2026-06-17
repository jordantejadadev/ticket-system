package com.jordan.ticket_system.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String asunto;

    @JsonIgnore
    @Column(unique = true)
    private String mensajeId;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String emailRemitente;

    @Enumerated(EnumType.STRING)
    private EstadoTicket estado;

    private LocalDateTime createdAt;

    private boolean seen = false;
}