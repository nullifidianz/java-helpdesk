package com.nullifidianz.helpdesk.Service;

import com.nullifidianz.helpdesk.DTO.Comment.CommentRequest;
import com.nullifidianz.helpdesk.DTO.Comment.CommentResponse;
import com.nullifidianz.helpdesk.Exception.ResourceNotFoundException;
import com.nullifidianz.helpdesk.Model.Comment;
import com.nullifidianz.helpdesk.Model.Ticket;
import com.nullifidianz.helpdesk.Model.User;
import com.nullifidianz.helpdesk.Repository.CommentRepository;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;

    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser();
    }

    @Transactional
    public CommentResponse createComment(Long ticketId, CommentRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado não encontrado com id: " + ticketId));

        Comment comment = new Comment();
        comment.setMensagem(request.getMensagem());
        comment.setAutor(getCurrentUser());
        comment.setTicket(ticket);

        comment = commentRepository.save(comment);
        return convertToResponse(comment);
    }

    public List<CommentResponse> getCommentsByTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Chamado não encontrado com id: " + ticketId));

        List<Comment> comments = commentRepository.findByTicketOrderByCreatedAtAsc(ticket);
        return comments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse convertToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setMensagem(comment.getMensagem());
        response.setAutorId(comment.getAutor().getId());
        response.setAutorName(comment.getAutor().getName());
        response.setAutorEmail(comment.getAutor().getEmail());
        response.setTicketId(comment.getTicket().getId());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
}

