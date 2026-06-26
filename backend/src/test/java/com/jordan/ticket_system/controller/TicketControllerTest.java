package com.jordan.ticket_system.controller;

import com.jordan.ticket_system.dto.TicketResponseDTO;
import com.jordan.ticket_system.dto.UpdateEstadoRequest;
import com.jordan.ticket_system.entity.EstadoTicket;
import com.jordan.ticket_system.security.JwtAuthenticationEntryPoint;
import com.jordan.ticket_system.security.JwtFilter;
import com.jordan.ticket_system.security.JwtService;
import com.jordan.ticket_system.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(TicketController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TicketService ticketService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldReturnTickets() throws Exception {

        TicketResponseDTO dto = new TicketResponseDTO(
                1L,
                "Problema con login",
                "No puedo iniciar sesión",
                "jordan@test.com",
                EstadoTicket.ABIERTO,
                false,
                LocalDateTime.now()
        );

        Page<TicketResponseDTO> page = new PageImpl<>(List.of(dto));

        when(ticketService.getTickets(
                0,
                5,
                null,
                null
        )).thenReturn(page);

        mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id")
                        .value(1))
                .andExpect(jsonPath("$.content[0].asunto")
                        .value("Problema con login"))
                .andExpect(jsonPath("$.content[0].descripcion")
                        .value("No puedo iniciar sesión"))
                .andExpect(jsonPath("$.content[0].emailRemitente")
                        .value("jordan@test.com"))
                .andExpect(jsonPath("$.content[0].estado")
                        .value("ABIERTO"))
                .andExpect(jsonPath("$.content[0].seen")
                        .value(false));

        verify(ticketService).getTickets(0, 5, null, null);
    }

    @Test
    void shouldReturnTicketById() throws Exception {

        Long id = 1L;

        TicketResponseDTO dto = new TicketResponseDTO(
                id,
                "Problema con login",
                "No puedo iniciar sesión",
                "jordan@test.com",
                EstadoTicket.ABIERTO,
                false,
                LocalDateTime.now()
        );

        when(ticketService.getTicketById(id))
                .thenReturn(dto);

        mockMvc.perform(get("/tickets/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.asunto")
                        .value("Problema con login"))
                .andExpect(jsonPath("$.descripcion")
                        .value("No puedo iniciar sesión"))
                .andExpect(jsonPath("$.emailRemitente")
                        .value("jordan@test.com"))
                .andExpect(jsonPath("$.estado")
                        .value("ABIERTO"))
                .andExpect(jsonPath("$.seen")
                        .value(false));

        verify(ticketService).getTicketById(id);
    }

    @Test
    void shouldUpdateEstadoSuccessfully() throws Exception {

        Long id = 1L;

        UpdateEstadoRequest request = new UpdateEstadoRequest();
        request.setEstado(EstadoTicket.EN_PROGRESO);

        TicketResponseDTO dto = new TicketResponseDTO(
                id,
                "Problema con login",
                "No puedo iniciar sesión",
                "jordan@test.com",
                EstadoTicket.EN_PROGRESO,
                false,
                LocalDateTime.now()
        );

        when(ticketService.updateEstado(id, EstadoTicket.EN_PROGRESO))
                .thenReturn(dto);

        mockMvc.perform(patch("/tickets/{id}/estado", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(id))
                .andExpect(jsonPath("$.estado")
                        .value("EN_PROGRESO"));

        verify(ticketService).updateEstado(id, EstadoTicket.EN_PROGRESO);
    }

    @Test
    void shouldDeleteTicketSuccessfully() throws Exception {

        Long id = 1L;

        doNothing().when(ticketService).deleteTicket(id);

        mockMvc.perform(delete("/tickets/{id}", id))
                .andExpect(status().isOk());

        verify(ticketService).deleteTicket(id);
    }

}
