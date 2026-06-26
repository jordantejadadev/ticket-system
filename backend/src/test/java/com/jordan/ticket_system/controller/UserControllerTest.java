package com.jordan.ticket_system.controller;

import com.jordan.ticket_system.dto.UpdateUserRequestDTO;
import com.jordan.ticket_system.dto.UserRequestDTO;
import com.jordan.ticket_system.dto.UserResponseDTO;
import com.jordan.ticket_system.entity.Role;
import com.jordan.ticket_system.security.JwtAuthenticationEntryPoint;
import com.jordan.ticket_system.security.JwtFilter;
import com.jordan.ticket_system.security.JwtService;
import com.jordan.ticket_system.service.UserService;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnUsers() throws Exception {

        UserResponseDTO dto = new UserResponseDTO(
                1L,
                "Jordan",
                "jordan@test.com",
                null
        );

        Page<UserResponseDTO> page = new PageImpl<>(List.of(dto));

        when(userService.getUsers(any(), eq("")))
                .thenReturn(page);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id")
                        .value(1))
                .andExpect(jsonPath("$.content[0].name")
                        .value("Jordan"))
                .andExpect(jsonPath("$.content[0].email")
                        .value("jordan@test.com"));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateUserSuccessfully() throws Exception {

        UserRequestDTO request = new UserRequestDTO();
        request.setName("Jordan");
        request.setEmail("jordan@test.com");
        request.setPassword("123456");
        request.setRole(Role.ADMIN);

        UserResponseDTO response = new UserResponseDTO(
                1L,
                "Jordan",
                "jordan@test.com",
                Role.ADMIN
        );

        when(userService.createUser(any(UserRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jordan"))
                .andExpect(jsonPath("$.email").value("jordan@test.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateUserSuccessfully() throws Exception {

        Long id = 1L;

        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setName("Jordan Updated");
        request.setEmail("nuevo@test.com");
        request.setRole(Role.ADMIN);

        UserResponseDTO response = new UserResponseDTO(
                id,
                "Jordan Updated",
                "nuevo@test.com",
                Role.ADMIN
        );

        when(userService.updateUser(eq(id), any(UpdateUserRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                put("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jordan Updated"))
                .andExpect(jsonPath("$.email").value("nuevo@test.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteUserSuccessfully() throws Exception {

        Long id = 1L;

        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(id);

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenNameIsBlank() throws Exception {

        UserRequestDTO request = new UserRequestDTO();
        request.setName("");
        request.setEmail("jordan@test.com");
        request.setPassword("123456");
        request.setRole(Role.ADMIN);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {

        UserRequestDTO request = new UserRequestDTO();
        request.setName("Jordan");
        request.setEmail("correo-invalido");
        request.setPassword("123456");
        request.setRole(Role.ADMIN);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenPasswordIsTooShort() throws Exception {

        UserRequestDTO request = new UserRequestDTO();
        request.setName("Jordan");
        request.setEmail("jordan@test.com");
        request.setPassword("123");
        request.setRole(Role.ADMIN);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenRoleIsNull() throws Exception {

        UserRequestDTO request = new UserRequestDTO();
        request.setName("Jordan");
        request.setEmail("jordan@test.com");
        request.setPassword("123456");
        request.setRole(null);


        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }

}
