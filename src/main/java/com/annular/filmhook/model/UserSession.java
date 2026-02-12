package com.annular.filmhook.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "user_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "session_token")
    private String sessionToken;

    @Column(name = "firebase_token", columnDefinition = "TEXT")
    private String firebaseToken;

    @Column(name = "device_name", columnDefinition = "TEXT")
    private String deviceName;

    @Column(name = "ip_address", columnDefinition = "TEXT")
    private String ipAddress;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "last_used_on")
    private Date lastUsedOn;
}
