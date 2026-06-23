package com.jordan.ticket_system.service.impl;

import com.jordan.ticket_system.dto.UpdateUserRequestDTO;
import com.jordan.ticket_system.dto.UserRequestDTO;
import com.jordan.ticket_system.dto.UserResponseDTO;
import com.jordan.ticket_system.entity.Role;
import com.jordan.ticket_system.entity.User;
import com.jordan.ticket_system.exception.ResourceAlreadyExistsException;
import com.jordan.ticket_system.exception.ResourceNotFoundException;
import com.jordan.ticket_system.mapper.UserMapper;
import com.jordan.ticket_system.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
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

    @Test
    void shouldReturnAllUsersWhenSearchIsEmpty() {

        Pageable pageable = PageRequest.of(0,5);

        User user = new User();
        user.setId(1L);
        user.setName("Jordan");
        user.setEmail("jordan@test.com");

        Page<User> users = new PageImpl<>(List.of(user));

        UserResponseDTO dto = new UserResponseDTO(
                1L,
                "Jordan",
                "jordan@test.com",
                null
        );

        when(userRepository.findAll(pageable))
                .thenReturn(users);

        when(userMapper.toDTO(user))
                .thenReturn(dto);

        Page<UserResponseDTO> result = userService.getUsers(pageable, "");

        assertEquals(1, result.getTotalElements());
        assertEquals("Jordan", result.getContent().get(0).getName());

        verify(userRepository).findAll(pageable);
        verify(userMapper).toDTO(user);
    }

    @Test
    void shouldReturnFilteredUsersWhenSearchIsProvided() {

        Pageable pageable = PageRequest.of(0,5);

        User user = new User();
        user.setId(1L);
        user.setName("Jordan");
        user.setEmail("jordan@test.com");

        Page<User> users = new PageImpl<>(List.of(user));

        UserResponseDTO dto = new UserResponseDTO(
                1L,
                "Jordan",
                "jordan@test.com",
                null
        );

        when(userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                "Jordan",
                "Jordan",
                pageable
        )).thenReturn(users);

        when(userMapper.toDTO(user))
                .thenReturn(dto);

        Page<UserResponseDTO> result = userService.getUsers(pageable, "Jordan");

        assertEquals(1, result.getTotalElements());

        verify(userRepository)
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "Jordan",
                        "Jordan",
                        pageable
                );

        verify(userMapper).toDTO(user);
    }

    @Test
    void shouldUpdateUserSuccessfully() {

        Long id = 1L;

        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setName("Jordan Updated");
        request.setEmail("nuevo@test.com");
        request.setPassword("123456");
        request.setRole(Role.ADMIN);

        User user = new User();
        user.setId(id);
        user.setName("Jordan");
        user.setEmail("viejo@test.com");
        user.setPassword("passwordViejo");
        user.setRole(Role.SUPPORT);

        User updatedUser = new User();
        updatedUser.setId(id);
        updatedUser.setName("Jordan Updated");
        updatedUser.setEmail("nuevo@test.com");
        updatedUser.setPassword("passwordEncriptado");
        updatedUser.setRole(Role.ADMIN);

        UserResponseDTO dto = new UserResponseDTO(
                id,
                "Jordan Updated",
                "nuevo@test.com",
                Role.ADMIN
        );

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("123456"))
                .thenReturn("passwordEncriptado");

        when(userRepository.save(user))
                .thenReturn(updatedUser);

        when(userMapper.toDTO(updatedUser))
                .thenReturn(dto);

        UserResponseDTO result = userService.updateUser(id, request);

        assertNotNull(result);
        assertEquals("Jordan Updated", result.getName());
        assertEquals("nuevo@test.com", result.getEmail());
        assertEquals(Role.ADMIN, result.getRole());

        verify(userRepository).findById(id);
        verify(passwordEncoder).encode("123456");
        verify(userRepository).save(user);
        verify(userMapper).toDTO(updatedUser);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingUser() {

        Long id = 1L;

        UpdateUserRequestDTO request = new UpdateUserRequestDTO();

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.updateUser(id, request)
        );

        verify(userRepository).findById(id);

        verify(userRepository, never()).save(any());

        verify(userMapper, never()).toDTO(any());
    }

    @Test
    void shouldUpdateUserWithoutChangingPassword() {

        Long id = 1L;

        UpdateUserRequestDTO request = new UpdateUserRequestDTO();
        request.setName("Jordan");
        request.setEmail("nuevo@test.com");
        request.setPassword("");
        request.setRole(Role.ADMIN);

        User user = new User();
        user.setId(id);
        user.setPassword("passwordViejo");

        UserResponseDTO dto = new UserResponseDTO(
                id,
                "Jordan",
                "nuevo@test.com",
                Role.ADMIN
        );

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toDTO(user))
                .thenReturn(dto);

        userService.updateUser(id, request);

        verify(passwordEncoder, never()).encode(any());

        verify(userRepository).save(user);
    }

    @Test
    void shouldDeleteUserSuccessfully() {

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.deleteUser(1l);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingUser() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.deleteUser(1L)
        );

        verify(userRepository).findById(1L);

        verify(userRepository, never()).delete(any(User.class));
    }


}
