package com.jordan.ticket_system.service.impl;

import com.jordan.ticket_system.dto.UserRequestDTO;
import com.jordan.ticket_system.dto.UserResponseDTO;
import com.jordan.ticket_system.entity.Role;
import com.jordan.ticket_system.entity.User;
import com.jordan.ticket_system.exception.ResourceAlreadyExistsException;
import com.jordan.ticket_system.mapper.UserMapper;
import com.jordan.ticket_system.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldCreateUserSuccessfully() {

        // Arrange
        UserRequestDTO request = new UserRequestDTO();
        request.setName("Jordan");
        request.setEmail("jordan@test.com");
        request.setPassword("123456");
        request.setRole(Role.ADMIN);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Jordan");
        savedUser.setEmail("jordan@test.com");
        savedUser.setPassword("password-encriptado");
        savedUser.setRole(Role.ADMIN);

        UserResponseDTO response = new UserResponseDTO(
                1L,
                "Jordan",
                "jordan@test.com",
                Role.ADMIN);

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("123456"))
                .thenReturn("password-encriptado");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(userMapper.toDTO(savedUser))
                .thenReturn(response);

        // Act
        UserResponseDTO result = userService.createUser(request);


        //Assert
        assertNotNull(result);
        assertEquals("Jordan", result.getName());
        assertEquals("jordan@test.com", result.getEmail());

        // Verify
        verify(userRepository).findByEmail(request.getEmail());
        verify(passwordEncoder).encode("123456");
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDTO(savedUser);
        
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        UserRequestDTO request = new UserRequestDTO();
        request.setEmail("jordan@test.com");

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(new User()));

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> userService.createUser(request)
        );

        verify(userRepository).findByEmail(request.getEmail());

        verify(userRepository, never()).save(any());
    }
}
