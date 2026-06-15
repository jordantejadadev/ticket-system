package com.jordan.ticket_system.service.impl;

import com.jordan.ticket_system.dto.TicketResponseDTO;
import com.jordan.ticket_system.entity.EstadoTicket;
import com.jordan.ticket_system.entity.Ticket;
import com.jordan.ticket_system.exception.ResourceNotFoundException;
import com.jordan.ticket_system.mapper.TicketMapper;
import com.jordan.ticket_system.repository.TicketRepository;
import com.jordan.ticket_system.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @Override
    public Page<TicketResponseDTO> getTickets(
            int page,
            int size,
            String estado,
            String search
    ) {

        PageRequest pageable = PageRequest.of(page, size);

        Page<Ticket> tickets;

        boolean hasEstado = estado != null && !estado.isBlank();
        boolean hasSearch = search != null && !search.isBlank();

        if (hasEstado && hasSearch) {

            tickets = ticketRepository
                    .findByEstadoAndAsuntoContainingIgnoreCaseOrderByCreatedAtDesc(
                            EstadoTicket.valueOf(estado),
                            search,
                            pageable
                    );
        }

        else if (hasEstado) {

            tickets = ticketRepository
                    .findByEstadoOrderByCreatedAtDesc(
                            EstadoTicket.valueOf(estado),
                            pageable
                    );
        }

        else if (hasSearch) {

            tickets = ticketRepository
                    .findByAsuntoContainingIgnoreCaseOrderByCreatedAtDesc(
                            search,
                            pageable
                    );
        } else {
            tickets = ticketRepository
                    .findAllByOrderByCreatedAtDesc(pageable);
        }

        return tickets.map(ticketMapper::toDTO);


    }

    @Override
    public TicketResponseDTO updateEstado(Long id, EstadoTicket estado) {

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ticket no encontrado")
                );

        ticket.setEstado(estado);

        return ticketMapper.toDTO(ticketRepository.save(ticket));
    }

    @Override
    public void deleteTicket(Long id) {

        if (!ticketRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ticket no encontrado");
        }

        ticketRepository.deleteById(id);
    }

    @Override
    public Map<String, Long> getStats() {

        Map<String, Long> stats = new HashMap<>();

        stats.put(
                "abiertos",
                ticketRepository.countByEstado(EstadoTicket.ABIERTO)
        );

        stats.put(
                "en_progreso",
                ticketRepository.countByEstado(EstadoTicket.EN_PROGRESO)
        );

        stats.put(
                "cerrados",
                ticketRepository.countByEstado(EstadoTicket.CERRADO)
        );

        return stats;
    }

    @Override
    public TicketResponseDTO getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado"));

        return ticketMapper.toDTO(ticket);
    }

    @Override
    public TicketResponseDTO markAsSeen(Long id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado"));

        ticket.setSeen(true);

        return ticketMapper.toDTO(ticketRepository.save(ticket));
    }


}