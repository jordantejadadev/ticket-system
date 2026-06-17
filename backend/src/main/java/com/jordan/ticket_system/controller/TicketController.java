package com.jordan.ticket_system.controller;

import com.jordan.ticket_system.dto.ErrorResponse;
import com.jordan.ticket_system.dto.TicketResponseDTO;
import com.jordan.ticket_system.dto.UpdateEstadoRequest;
import com.jordan.ticket_system.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(
        name = "Tickets",
        description = "Operaciones para consultar y administrar tickets."
)
@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
//@SecurityRequirement(name = "bearerAuth")
public class TicketController {

    private final TicketService ticketService;

    @Operation(
            summary = "Listar tickets",
            description = "Obtiene una lista paginada de tickets con filtros opcionales."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tickets obtenidos correctamente"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @GetMapping
    public Page<TicketResponseDTO> getTickets(
            @Parameter(
                    description = "Número de página",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,

            @Parameter(
                    description = "Cantidad de registros por página",
                    example = "5"
            )
            @RequestParam(defaultValue = "5") int size,

            @Parameter(
                    description = "Filtrar por estado",
                    example = "ABIERTO"
            )
            @RequestParam(required = false) String estado,

            @Parameter(
                    description = "Buscar por asunto, remitente o correo",
                    example = "Juan"
            )
            @RequestParam(required = false) String search
    ) {

        return ticketService.getTickets(
                page,
                size,
                estado,
                search
        );
    }

    @Operation(
            summary = "Obtener ticket por ID",
            description = "Devuelve la información de un ticket específico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket encontrado"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
    })
    @GetMapping("/{id}")
    public TicketResponseDTO getTicketById(
            @Parameter(
                    description = "ID del ticket",
                    example = "1"
            )
            @PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    @Operation(
            summary = "Actualizar estado del ticket",
            description = "Modifica el estado de un ticket."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                        "status": 400,
                                        "message": "Errores de validación",
                                        "timestamp": "2026-06-16T14:30:15",
                                        "errors": {
                                            "estado": "El estado es obligatorio"
                                        }
                                    }
                                    """
                    )
            )),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                            value = """
                                    {
                                        "status": 404,
                                        "message": "Ticket no encontrado",
                                        "timestamp": "2026-06-16T14:30:15"
                                    }
                                    """
                    )
            ))
    })
    @PatchMapping("/{id}/estado")
    public TicketResponseDTO updateEstado(
            @Parameter(
                    description = "ID del ticket",
                    example = "1"
            )
            @PathVariable Long id,
            @Valid @RequestBody UpdateEstadoRequest request
    ) {

        return ticketService.updateEstado(
                id,
                request.getEstado()
        );
    }

    @Operation(
            summary = "Eliminar ticket",
            description = "Elimina un ticket del sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ticket eliminado"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @DeleteMapping("/{id}")
    public void deleteTicket(
            @Parameter(
                    description = "ID del ticket",
                    example = "1"
            )
            @PathVariable Long id) {

        ticketService.deleteTicket(id);
    }

    @Operation(
            summary = "Obtener estadísticas",
            description = "Devuelve estadísticas generales de los tickets."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Estadísticas obtenidas correctamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/stats")
    public Map<String, Long> getStats() {

        return ticketService.getStats();
    }

    @Operation(
            summary = "Marcar ticket como visto",
            description = "Marca un ticket como visto"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ticket actualizado"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado")
    })
    @PatchMapping("/{id}/seen")
    public TicketResponseDTO markAsSeen(
            @Parameter(
                    description = "ID del ticket",
                    example = "1"
            )
            @PathVariable Long id) {
        return ticketService.markAsSeen(id);
    }
}