package com.annular.filmhook.security.jwt;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JwtResponse {

    private String jwt;
    private Integer id;
    private String username;
    private String email;
    private String message;
    private Integer status;
    private String token;
    private String userType;
    private String filmHookCode;
    private float review;
    private String lastName;
    private String profilePic;

}
