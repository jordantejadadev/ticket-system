package com.jordan.ticket_system.service.impl;

import com.jordan.ticket_system.dto.UpdateUserRequestDTO;
import com.jordan.ticket_system.dto.UserRequestDTO;
import com.jordan.ticket_system.dto.UserResponseDTO;
import com.jordan.ticket_system.entity.User;
import com.jordan.ticket_system.mapper.UserMapper;
import com.jordan.ticket_system.repository.UserRepository;
import com.jordan.ticket_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

//    @Override
//    public Page<User> getUsers(Pageable pageable) {
//        return userRepository.findAll(pageable);
//    }

    @Override
    public Page<UserResponseDTO> getUsers(Pageable pageable, String search) {

        Page<User> users;

        if(search == null || search.isEmpty()) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);
        }

        return users.map(userMapper::toDTO);
    }

    @Override
    public User createUser(UserRequestDTO request) {
        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email ya registrado");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User updateUser(Long id, UpdateUserRequestDTO request) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encotnrado"));

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        if(request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(user);
    }

}
