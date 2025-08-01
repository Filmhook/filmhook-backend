package com.annular.filmhook.webmodel;

import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
public class RecentUserWebModel {
    private Integer userId;
    private String name;
    private String userType;
    private String profilePicUrl;
    private String source; // "search" or "chat"
    private String lastInteractionTime;
    private Float review;
    private Boolean pinProfile; // Already present in service logic
    private Boolean common;  
                            // New field: true if 3 are pinned, else false
}