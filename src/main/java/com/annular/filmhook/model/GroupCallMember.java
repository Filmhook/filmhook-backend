package com.annular.filmhook.model;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "group_call_members")
@Data
public class GroupCallMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer groupCallId;
    private Integer userId;

    @Column(columnDefinition = "TEXT")
    private String rtcToken;

    private Boolean joined = false;

    private LocalDateTime joinTime;
    private LocalDateTime leaveTime;
    @Column(name = "deleted")
    private Boolean deleted = false;
}