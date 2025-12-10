package com.nullifidianz.helpdesk.DTO.Ticket;

import com.nullifidianz.helpdesk.Model.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StatusUpdateRequest {
    @NotNull(message = "Status é obrigatório")
    private TicketStatus status;
}

