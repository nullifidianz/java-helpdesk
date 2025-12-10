package com.nullifidianz.helpdesk.Controller;

import com.nullifidianz.helpdesk.DTO.Ticket.StatusUpdateRequest;
import com.nullifidianz.helpdesk.DTO.Ticket.TicketRequest;
import com.nullifidianz.helpdesk.DTO.Ticket.TicketResponse;
import com.nullifidianz.helpdesk.Service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        List<TicketResponse> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest request) {
        TicketResponse response = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        TicketResponse response = ticketService.getTicketById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        TicketResponse response = ticketService.updateStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<TicketResponse> assignAgent(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Long> body) {
        Long agentId = body != null ? body.get("agentId") : null;
        TicketResponse response = ticketService.assignAgent(id, agentId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/information")
    public ResponseEntity<TicketResponse> addInformation(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String additionalInfo = body.get("additionalInfo");
        if (additionalInfo == null || additionalInfo.isBlank()) {
            throw new IllegalArgumentException("additionalInfo é obrigatório");
        }
        TicketResponse response = ticketService.addInformation(id, additionalInfo);
        return ResponseEntity.ok(response);
    }
}

