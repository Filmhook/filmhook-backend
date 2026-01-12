package com.annular.filmhook.model;

import java.time.LocalDateTime;

import javax.persistence.*;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "admin_user")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fullName;
    private String email;
    private String phoneNumber;
    private String jobTitle;
    private String organizationUnit;
    private String password;
    private String profilePhotoUrl;

    private Boolean isEmailVerified;
    private Integer emailOtp;
    private LocalDateTime otpExpiry;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private AdminRole role;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

