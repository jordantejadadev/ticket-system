package com.jordan.ticket_system.controller;

import com.jordan.ticket_system.dto.TicketResponseDTO;
import com.jordan.ticket_system.dto.UpdateEstadoRequest;
import com.jordan.ticket_system.entity.Ticket;
import com.jordan.ticket_system.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public Page<TicketResponseDTO> getTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String search
    ) {

        return ticketService.getTickets(
                page,
                size,
                estado,
                search
        );
    }

    @GetMapping("/{id}")
    public TicketResponseDTO getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    @PatchMapping("/{id}/estado")
    public TicketResponseDTO updateEstado(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEstadoRequest request
    ) {

        return ticketService.updateEstado(
                id,
                request.getEstado()
        );
    }

    @DeleteMapping("/{id}")
    public void deleteTicket(@PathVariable Long id) {

        ticketService.deleteTicket(id);
    }

    @GetMapping("/stats")
    public Map<String, Long> getStats() {

        return ticketService.getStats();
    }

    @PatchMapping("/{id}/seen")
    public TicketResponseDTO markAsSeen(@PathVariable Long id) {
        return ticketService.markAsSeen(id);
    }
}