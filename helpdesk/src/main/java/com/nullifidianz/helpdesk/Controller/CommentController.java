package com.nullifidianz.helpdesk.Controller;

import com.nullifidianz.helpdesk.DTO.Comment.CommentRequest;
import com.nullifidianz.helpdesk.DTO.Comment.CommentResponse;
import com.nullifidianz.helpdesk.Service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{ticketId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long ticketId,
            @Valid @RequestBody CommentRequest request) {
        CommentResponse response = commentService.createComment(ticketId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{ticketId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByTicket(@PathVariable Long ticketId) {
        List<CommentResponse> comments = commentService.getCommentsByTicket(ticketId);
        return ResponseEntity.ok(comments);
    }
}

