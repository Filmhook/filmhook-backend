package com.annular.filmhook.model;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.*;
@Entity
@Table(name = "user_search_history", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "searched_user_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "searched_user_id", nullable = false)
    private Integer searchedUserId;

    @Column(name = "searched_at")
    private LocalDateTime searchedAt;

    @Column(name = "source", nullable = false)
    private String source; // "search" or "chat"
    
    @Column(name= "pin_profile", nullable = false)
    private Boolean pinProfile;
}
