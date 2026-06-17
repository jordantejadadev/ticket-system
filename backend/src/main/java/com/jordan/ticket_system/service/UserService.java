package com.jordan.ticket_system.service;
import com.jordan.ticket_system.dto.UpdateUserRequestDTO;
import com.jordan.ticket_system.dto.UserRequestDTO;
import com.jordan.ticket_system.dto.UserResponseDTO;
import com.jordan.ticket_system.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

//    Page<User> getUsers(Pageable pageable);

    Page<UserResponseDTO> getUsers(Pageable pageable, String search);

    UserResponseDTO createUser(UserRequestDTO request);

    void deleteUser(Long id);

    UserResponseDTO updateUser(Long id, UpdateUserRequestDTO request);
}
