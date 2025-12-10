package com.nullifidianz.helpdesk.Service;

import com.nullifidianz.helpdesk.DTO.Ticket.StatusUpdateRequest;
import com.nullifidianz.helpdesk.DTO.Ticket.TicketRequest;
import com.nullifidianz.helpdesk.DTO.Ticket.TicketResponse;
import com.nullifidianz.helpdesk.Exception.BusinessException;
import com.nullifidianz.helpdesk.Exception.ResourceNotFoundException;
import com.nullifidianz.helpdesk.Model.Ticket;
import com.nullifidianz.helpdesk.Model.TicketStatus;
import com.nullifidianz.helpdesk.Model.User;
import com.nullifidianz.helpdesk.Model.UserRoles;
import com.nullifidianz.helpdesk.Repository.TicketRepository;
import com.nullifidianz.helpdesk.Security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserService userService;

    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser();
    }

    @Transactional
    public TicketResponse createTicket(TicketRequest request) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != UserRoles.CLIENTE) {
            throw new BusinessException("Apenas clientes podem criar chamados");
        }

        Ticket ticket = new Ticket();
        ticket.setTitulo(request.getTitulo());
        ticket.setDescricao(request.getDescricao());
        ticket.setCategoria(request.getCategoria());
        ticket.setStatus(TicketStatus.ABERTO);
        ticket.setCliente(currentUser);

        ticket = ticketRepository.save(ticket);
        return convertToResponse(ticket);
    }

    public List<TicketResponse> getAllTickets() {
        User currentUser = getCurrentUser();
        List<Ticket> tickets;

        if (currentUser.getRole() == UserRoles.CLIENTE) {
            tickets = ticketRepository.findByCliente(currentUser);
        } else if (currentUser.getRole() == UserRoles.AGENTE) {
            tickets = ticketRepository.findByClienteOrAgente(currentUser);
        } else { // ADMIN
            tickets = ticketRepository.findAll();
        }

        return tickets.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public TicketResponse getTicketById(Long id) {
        User currentUser = getCurrentUser();
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado não encontrado com id: " + id));

        // Validação de acesso: CLIENTE só pode ver seus próprios chamados
        if (currentUser.getRole() == UserRoles.CLIENTE && !ticket.getCliente().getId().equals(currentUser.getId())) {
            throw new BusinessException("Você não tem permissão para visualizar este chamado");
        }

        return convertToResponse(ticket);
    }

    @Transactional
    public TicketResponse updateStatus(Long id, StatusUpdateRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado não encontrado com id: " + id));

        TicketStatus newStatus = request.getStatus();
        TicketStatus currentStatus = ticket.getStatus();

        // Validação: não pode reaberto depois de fechado
        if (currentStatus == TicketStatus.FECHADO) {
            throw new BusinessException("Chamado fechado não pode ser alterado");
        }

        // Validação: só pode fechar se estiver RESOLVIDO
        if (newStatus == TicketStatus.FECHADO && currentStatus != TicketStatus.RESOLVIDO) {
            throw new BusinessException("Chamado só pode ser fechado se estiver como RESOLVIDO");
        }

        // Validação de transições válidas
        validateStatusTransition(currentStatus, newStatus);

        ticket.setStatus(newStatus);
        ticket = ticketRepository.save(ticket);

        return convertToResponse(ticket);
    }

    @Transactional
    public TicketResponse assignAgent(Long id, Long agentId) {
        User currentUser = getCurrentUser();
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado não encontrado com id: " + id));

        // Apenas AGENTE pode assumir um chamado
        if (currentUser.getRole() == UserRoles.AGENTE) {
            if (ticket.getAgente() != null && !ticket.getAgente().getId().equals(currentUser.getId())) {
                throw new BusinessException("Chamado já está atribuído a outro agente");
            }
            ticket.setAgente(currentUser);
            ticket.setStatus(TicketStatus.EM_ATENDIMENTO);
        } else if (currentUser.getRole() == UserRoles.ADMIN) {
            // ADMIN pode reatribuir
            if (agentId == null) {
                throw new BusinessException("ADMIN deve fornecer o ID do agente para atribuir");
            }
            User agent = userService.findById(agentId);
            if (agent.getRole() != UserRoles.AGENTE) {
                throw new BusinessException("Usuário deve ser um AGENTE");
            }
            ticket.setAgente(agent);
            if (ticket.getStatus() == TicketStatus.ABERTO) {
                ticket.setStatus(TicketStatus.EM_ATENDIMENTO);
            }
        } else {
            throw new BusinessException("Apenas AGENTES ou ADMIN podem atribuir chamados");
        }

        ticket = ticketRepository.save(ticket);
        return convertToResponse(ticket);
    }

    @Transactional
    public TicketResponse addInformation(Long id, String additionalInfo) {
        User currentUser = getCurrentUser();
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado não encontrado com id: " + id));

        // Validação: CLIENTE só pode adicionar informações quando ABERTO
        if (currentUser.getRole() == UserRoles.CLIENTE) {
            if (!ticket.getCliente().getId().equals(currentUser.getId())) {
                throw new BusinessException("Você não tem permissão para modificar este chamado");
            }
            if (ticket.getStatus() != TicketStatus.ABERTO) {
                throw new BusinessException("Informações só podem ser adicionadas quando o chamado está ABERTO");
            }
        }

        ticket.setDescricao(ticket.getDescricao() + "\n\n[Informação adicional]: " + additionalInfo);
        ticket = ticketRepository.save(ticket);

        return convertToResponse(ticket);
    }

    private void validateStatusTransition(TicketStatus current, TicketStatus newStatus) {
        // Validações de transições permitidas
        if (current == TicketStatus.ABERTO && newStatus == TicketStatus.FECHADO) {
            throw new BusinessException("Não é possível fechar um chamado diretamente de ABERTO");
        }
        // Outras validações podem ser adicionadas aqui
    }

    private TicketResponse convertToResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTitulo(ticket.getTitulo());
        response.setDescricao(ticket.getDescricao());
        response.setCategoria(ticket.getCategoria());
        response.setStatus(ticket.getStatus());
        response.setCreatedAt(ticket.getCreatedAt());
        response.setUpdatedAt(ticket.getUpdatedAt());
        response.setClienteId(ticket.getCliente().getId());
        response.setClienteName(ticket.getCliente().getName());
        response.setClienteEmail(ticket.getCliente().getEmail());

        if (ticket.getAgente() != null) {
            response.setAgenteId(ticket.getAgente().getId());
            response.setAgenteName(ticket.getAgente().getName());
            response.setAgenteEmail(ticket.getAgente().getEmail());
        }

        return response;
    }
}

