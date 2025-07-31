package com.annular.filmhook.webmodel;

import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
public class RecentUserWebModel {
    private Integer userId;
    private String name;
    private String userType;
    private String profilePic; 
    private String source; // "search" or "chat"
    private LocalDateTime lastInteractionTime;
    private String review;
}