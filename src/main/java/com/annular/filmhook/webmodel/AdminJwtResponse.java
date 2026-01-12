package com.annular.filmhook.webmodel;

import lombok.*;

@Builder
@Getter
@Setter
public class AdminJwtResponse {

    private String jwt;
    private Integer id;
    private String refreshToken;
    private String email;
    private String fullName;
    private String role;
    private Integer status;
    private String message;
}
