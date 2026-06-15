package com.jordan.ticket_system.mapper;

import com.jordan.ticket_system.dto.UserResponseDTO;
import com.jordan.ticket_system.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
