package com.annular.filmhook.model;

import javax.persistence.Entity;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "admin_online_sessions")
@Data
public class AdminOnlineSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer adminId;

    private LocalDateTime loginTime;

    private LocalDateTime logoutTime; // nullable until offline
}
