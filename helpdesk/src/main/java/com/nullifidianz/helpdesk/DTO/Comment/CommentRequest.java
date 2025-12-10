package com.nullifidianz.helpdesk.DTO.Comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentRequest {
    @NotBlank(message = "Mensagem é obrigatória")
    private String mensagem;
}

