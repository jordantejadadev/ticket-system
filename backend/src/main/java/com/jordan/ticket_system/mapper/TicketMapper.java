package com.jordan.ticket_system.mapper;

import com.jordan.ticket_system.dto.TicketResponseDTO;
import com.jordan.ticket_system.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketResponseDTO toDTO(Ticket ticket) {
        return new TicketResponseDTO(
                ticket.getId(),
                ticket.getAsunto(),
                ticket.getDescripcion(),
                ticket.getEmailRemitente(),
                ticket.getEstado(),
                ticket.isSeen(),
                ticket.getCreatedAt()
        );
    }
}
