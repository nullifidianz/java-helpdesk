package com.nullifidianz.helpdesk.Repository;

import com.nullifidianz.helpdesk.Model.Ticket;
import com.nullifidianz.helpdesk.Model.TicketStatus;
import com.nullifidianz.helpdesk.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    List<Ticket> findByCliente(User cliente);
    
    List<Ticket> findByAgente(User agente);
    
    List<Ticket> findByStatus(TicketStatus status);
    
    @Query("SELECT t FROM Ticket t WHERE t.cliente = :user OR t.agente = :user")
    List<Ticket> findByClienteOrAgente(@Param("user") User user);
    
    @Query("SELECT t FROM Ticket t WHERE t.cliente = :cliente AND t.id = :id")
    Optional<Ticket> findByIdAndCliente(@Param("id") Long id, @Param("cliente") User cliente);
}

