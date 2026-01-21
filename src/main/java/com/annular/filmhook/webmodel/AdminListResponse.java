package com.annular.filmhook.webmodel;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminListResponse {

    private Integer adminId;
    private String name;
    private String email;
    private String role;   // Admin / Super Admin

    private boolean onlineStatus;
    private String dailyHours;
    private int workDone;
}