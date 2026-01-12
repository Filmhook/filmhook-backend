package com.annular.filmhook.webmodel;

import lombok.*;

@Getter
@Setter
public class AdminLoginRequest {
    private String email;
    private String password;
}