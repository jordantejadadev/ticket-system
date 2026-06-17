package com.jordan.ticket_system.controller;

import com.jordan.ticket_system.dto.ErrorResponse;
import com.jordan.ticket_system.dto.UpdateUserRequestDTO;
import com.jordan.ticket_system.dto.UserRequestDTO;
import com.jordan.ticket_system.dto.UserResponseDTO;
import com.jordan.ticket_system.entity.User;
import com.jordan.ticket_system.repository.UserRepository;
import com.jordan.ticket_system.service.UserService;
import com.jordan.ticket_system.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
     name = "Users",
     description = "Operaciones para administrar usuarios del sistema."
)
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin("*")
//@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

//    @GetMapping
//    public Page<User> getUsers(Pageable pageable) {
//        return userService.getUsers(pageable);
//    }

    @Operation(
            summary = "Listar usuarios",
            description = "Obtiene una lista paginada de usuarios con búsqueda opcional."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuarios obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping
    public Page<UserResponseDTO> getUsers(@ParameterObject Pageable pageable,
                                          @Parameter(
                                                  description = "Texto para buscar nombre o correo electrónico",
                                                  example = "Jordan"
                                          )
                                          @RequestParam(defaultValue = "") String search) {
        return userService.getUsers(pageable, search);
    }

    @Operation(
            summary = "Crear usuario",
            description = "Crea un nuevo usuario en el sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            )),
            @ApiResponse(responseCode = "409", description = "El correo ya existe",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO request) {
        return userService.createUser(request);
    }


    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario por su identificador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(
                    description = "ID del usuario",
                    example = "1"
            )
            @PathVariable Long id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza la información de un usuario existente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(
            @Parameter(
                    description = "ID del usuario",
                    example = "1"
            )
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequestDTO request) {
        return userService.updateUser(id, request);
    }

}
