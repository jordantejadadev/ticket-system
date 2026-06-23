package com.jordan.ticket_system.service.impl;

import com.jordan.ticket_system.dto.TicketResponseDTO;
import com.jordan.ticket_system.entity.EstadoTicket;
import com.jordan.ticket_system.entity.Ticket;
import com.jordan.ticket_system.exception.ResourceNotFoundException;
import com.jordan.ticket_system.mapper.TicketMapper;
import com.jordan.ticket_system.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Test
    void shouldReturnAllTicketsWhenNoFilterAreProvided() {

        PageRequest pageable = PageRequest.of(0, 5);

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setAsunto("No puedo iniciar sesión");

        Page<Ticket> tickets = new PageImpl<>(List.of(ticket));

        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setId(1L);
        dto.setAsunto("No puedo iniciar sesión");

        when(ticketRepository.findAllByOrderByCreatedAtDesc(pageable))
                .thenReturn(tickets);

        when(ticketMapper.toDTO(ticket))
                .thenReturn(dto);

        Page<TicketResponseDTO> result = ticketService.getTickets(
                0,
                5,
                null,
                null
        );

        assertEquals(1, result.getTotalElements());

        verify(ticketRepository).findAllByOrderByCreatedAtDesc(pageable);

        verify(ticketMapper).toDTO(ticket);
    }

    @Test
    void shouldReturnTicketsFileteredByEstado() {

        PageRequest pageable = PageRequest.of(0, 5);

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setEstado(EstadoTicket.ABIERTO);

        Page<Ticket> tickets = new PageImpl<>(List.of(ticket));

        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setId(1L);
        dto.setEstado(EstadoTicket.ABIERTO);

        when(ticketRepository.findByEstadoOrderByCreatedAtDesc(EstadoTicket.ABIERTO, pageable))
                .thenReturn(tickets);

        when(ticketMapper.toDTO(ticket))
                .thenReturn(dto);

        Page<TicketResponseDTO> result = ticketService.getTickets(
                0,
                5,
                "ABIERTO",
                null
        );

        assertEquals(1, result.getTotalElements());

        verify(ticketRepository).findByEstadoOrderByCreatedAtDesc(
                EstadoTicket.ABIERTO,
                pageable
        );

        verify(ticketMapper)
                .toDTO(ticket);

    }

    @Test
    void shouldReturnTicketsFilteredBySearch() {

        PageRequest pageable = PageRequest.of(0, 5);

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setAsunto("No puedo iniciar sesión");

        Page<Ticket> tickets = new PageImpl<>(List.of(ticket));

        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setId(1L);
        dto.setAsunto("No puedo iniciar sesión");

        when(ticketRepository.findByAsuntoContainingIgnoreCaseOrderByCreatedAtDesc(
                "sesión",
                pageable
        )).thenReturn(tickets);

        when(ticketMapper.toDTO(ticket))
                .thenReturn(dto);

        Page<TicketResponseDTO> result = ticketService.getTickets(
                0,
                5,
                null,
                "sesión"
        );

        assertEquals(1, result.getTotalElements());

        verify(ticketRepository)
                .findByAsuntoContainingIgnoreCaseOrderByCreatedAtDesc(
                        "sesión",
                        pageable
                );

        verify(ticketMapper)
                .toDTO(ticket);

    }

    @Test
    void shouldReturnTicketsFilteredByEstadoAndSearch() {

        PageRequest pageable = PageRequest.of(0, 5);

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setAsunto("No puedo iniciar sesión");
        ticket.setEstado(EstadoTicket.ABIERTO);

        Page<Ticket> tickets = new PageImpl<>(List.of(ticket));

        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setId(1L);
        dto.setAsunto("No puedo iniciar sesión");
        dto.setEstado(EstadoTicket.ABIERTO);

        when(ticketRepository.findByEstadoAndAsuntoContainingIgnoreCaseOrderByCreatedAtDesc(
                EstadoTicket.ABIERTO,
                "sesión",
                pageable
        )).thenReturn(tickets);

        when(ticketMapper.toDTO(ticket))
                .thenReturn(dto);

        Page<TicketResponseDTO> result = ticketService.getTickets(
                0,
                5,
                "ABIERTO",
                "sesión"
        );

        assertEquals(1, result.getTotalElements());

        verify(ticketRepository)
                .findByEstadoAndAsuntoContainingIgnoreCaseOrderByCreatedAtDesc(
                        EstadoTicket.ABIERTO,
                        "sesión",
                        pageable
                );

        verify(ticketMapper)
                .toDTO(ticket);
    }

    @Test
    void shouldUpdateTicketEstadoSuccessfully() {

        Long id = 1L;

        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setEstado(EstadoTicket.ABIERTO);

        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setId(id);
        dto.setEstado(EstadoTicket.CERRADO);

        when(ticketRepository.findById(id))
                .thenReturn(Optional.of(ticket));

        when(ticketRepository.save(ticket))
                .thenReturn(ticket);

        when(ticketMapper.toDTO(ticket))
                .thenReturn(dto);

        TicketResponseDTO result = ticketService.updateEstado(id, EstadoTicket.CERRADO);

        assertEquals(EstadoTicket.CERRADO, ticket.getEstado());
        assertEquals(EstadoTicket.CERRADO, result.getEstado());

        verify(ticketRepository).findById(id);
        verify(ticketRepository).save(ticket);
        verify(ticketMapper).toDTO(ticket);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingEstadoOfNonExistingTicket() {

        Long id = 1L;

        when(ticketRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> ticketService.updateEstado(id, EstadoTicket.CERRADO)
        );

        verify(ticketRepository).findById(id);

        verify(ticketRepository, never()).save(any());

        verify(ticketMapper, never()).toDTO(any());
    }

    @Test
    void shouldReturnTicketById() {

        Long id = 1L;

        Ticket ticket = new Ticket();
        ticket.setId(id);

        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setId(id);

        when(ticketRepository.findById(id))
                .thenReturn(Optional.of(ticket));

        when(ticketMapper.toDTO(ticket))
                .thenReturn(dto);

        TicketResponseDTO result = ticketService.getTicketById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());

        verify(ticketRepository).findById(id);
        verify(ticketMapper).toDTO(ticket);
    }

    @Test
    void shouldThrowExceptionWhenTicketDoesNotExist() {

        Long id = 1L;

        when(ticketRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> ticketService.getTicketById(id)
        );

        verify(ticketRepository).findById(id);

        verify(ticketMapper, never()).toDTO(any());
    }

    @Test
    void shouldMarkTicketAsSeen() {

        Long id = 1L;

        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setSeen(false);

        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setId(id);
        dto.setSeen(true);

        when(ticketRepository.findById(id))
                .thenReturn(Optional.of(ticket));

        when(ticketRepository.save(ticket))
                .thenReturn(ticket);

        when(ticketMapper.toDTO(ticket))
                .thenReturn(dto);

        TicketResponseDTO result = ticketService.markAsSeen(id);

        assertTrue(ticket.isSeen());
        assertTrue(result.isSeen());

        verify(ticketRepository).save(ticket);
        verify(ticketMapper).toDTO(ticket);
    }

    @Test
    void shouldThrowExceptionWhenMarkingNonExistingTicket() {
        Long id = 1L;

        when(ticketRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> ticketService.markAsSeen(id)
        );

        verify(ticketRepository).findById(id);

        verify(ticketRepository, never()).save(any());

        verify(ticketMapper, never()).toDTO(any());
    }

    @Test
    void shouldDeleteTicketSuccessfully() {
        Long id = 1L;

        when(ticketRepository.existsById(id))
                .thenReturn(true);

        ticketService.deleteTicket(id);

        verify(ticketRepository).existsById(id);

        verify(ticketRepository).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingTicket() {

        Long id = 1L;

        when(ticketRepository.existsById(id))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> ticketService.deleteTicket(id)
        );

        verify(ticketRepository).existsById(id);

        verify(ticketRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldReturnTicketStatistics() {
        when(ticketRepository.countByEstado(EstadoTicket.ABIERTO))
                .thenReturn(5L);

        when(ticketRepository.countByEstado(EstadoTicket.EN_PROGRESO))
                .thenReturn(3L);

        when(ticketRepository.countByEstado(EstadoTicket.CERRADO))
                .thenReturn(8L);

        Map<String, Long> stats = ticketService.getStats();

        assertEquals(5L, stats.get("abiertos"));
        assertEquals(3L, stats.get("en_progreso"));
        assertEquals(8L, stats.get("cerrados"));

        verify(ticketRepository).countByEstado(EstadoTicket.ABIERTO);
        verify(ticketRepository).countByEstado(EstadoTicket.EN_PROGRESO);
        verify(ticketRepository).countByEstado(EstadoTicket.CERRADO);
    }
}
