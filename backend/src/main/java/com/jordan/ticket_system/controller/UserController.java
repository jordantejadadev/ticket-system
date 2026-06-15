package com.jordan.ticket_system.controller;

import com.jordan.ticket_system.dto.UpdateUserRequestDTO;
import com.jordan.ticket_system.dto.UserRequestDTO;
import com.jordan.ticket_system.dto.UserResponseDTO;
import com.jordan.ticket_system.entity.User;
import com.jordan.ticket_system.repository.UserRepository;
import com.jordan.ticket_system.service.UserService;
import com.jordan.ticket_system.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserServiceImpl userService;

//    @GetMapping
//    public Page<User> getUsers(Pageable pageable) {
//        return userService.getUsers(pageable);
//    }

    @GetMapping
    public Page<UserResponseDTO> getUsers(Pageable pageable, @RequestParam(defaultValue = "") String search) {
        return userService.getUsers(pageable, search);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody UserRequestDTO request) {
        return userService.createUser(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id,@Valid @RequestBody UpdateUserRequestDTO request) {
        return userService.updateUser(id, request);
    }

}
