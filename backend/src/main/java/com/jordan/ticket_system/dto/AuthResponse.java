package com.jordan.ticket_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthResponse {
// Token LocalStorage
//    private String token;
//    private String role;

    //CookitHttpOnly
    private String name;
    private String email;
    private String role;
}
