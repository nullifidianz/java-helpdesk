package com.nullifidianz.helpdesk.DTO.Auth;

import com.nullifidianz.helpdesk.Model.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthResponse {
    private String token;
    private Long userId;
    private String name;
    private String email;
    private UserRoles role;
}

