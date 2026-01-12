package com.annular.filmhook.model;

import java.time.LocalDateTime;

import javax.persistence.*;
import javax.persistence.Table;

import lombok.*;

@Entity
@Table(name = "admin_refresh_token")
@Getter
@Setter
public class AdminRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer adminId;

    private String token;

    private LocalDateTime expiryDate;
}

