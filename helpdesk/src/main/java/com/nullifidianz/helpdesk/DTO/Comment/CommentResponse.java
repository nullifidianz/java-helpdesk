package com.nullifidianz.helpdesk.DTO.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentResponse {
    private Long id;
    private String mensagem;
    private Long autorId;
    private String autorName;
    private String autorEmail;
    private Long ticketId;
    private LocalDateTime createdAt;
}

