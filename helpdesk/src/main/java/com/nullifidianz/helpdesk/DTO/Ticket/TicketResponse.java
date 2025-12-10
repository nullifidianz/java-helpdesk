package com.nullifidianz.helpdesk.DTO.Ticket;

import com.nullifidianz.helpdesk.Model.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TicketResponse {
    private Long id;
    private String titulo;
    private String descricao;
    private String categoria;
    private TicketStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long clienteId;
    private String clienteName;
    private String clienteEmail;
    private Long agenteId;
    private String agenteName;
    private String agenteEmail;
}

