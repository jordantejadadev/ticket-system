package com.jordan.ticket_system.service;

import com.jordan.ticket_system.dto.TicketResponseDTO;
import com.jordan.ticket_system.entity.EstadoTicket;
import com.jordan.ticket_system.entity.Ticket;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface TicketService {

    Page<TicketResponseDTO> getTickets(
            int page,
            int size,
            String estado,
            String search
    );

    TicketResponseDTO updateEstado(Long id, EstadoTicket estado);

    void deleteTicket(Long id);

    Map<String, Long> getStats();

    TicketResponseDTO getTicketById(Long id);

    TicketResponseDTO markAsSeen(Long id);
}