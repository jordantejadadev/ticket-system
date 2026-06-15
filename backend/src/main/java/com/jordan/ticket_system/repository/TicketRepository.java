package com.jordan.ticket_system.repository;

import com.jordan.ticket_system.entity.EstadoTicket;
import com.jordan.ticket_system.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findAllByOrderByCreatedAtDesc(
            Pageable pageable
    );

    Page<Ticket> findByEstadoOrderByCreatedAtDesc(
            EstadoTicket estado,
            Pageable pageable
    );

    Page<Ticket> findByAsuntoContainingIgnoreCaseOrderByCreatedAtDesc(
            String search,
            Pageable pageable
    );

    Page<Ticket> findByEstadoAndAsuntoContainingIgnoreCaseOrderByCreatedAtDesc(
            EstadoTicket estado,
            String search,
            Pageable pageable
    );

    long countByEstado(EstadoTicket estado);

    boolean existsByMensajeId(String mensajeId);

}