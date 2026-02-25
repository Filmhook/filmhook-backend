package com.annular.filmhook.model;

import javax.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_call")
@Data
public class GroupCall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer hostUserId;

    @Column(nullable = false)
    private String channelName;

    private String callType;      // voice / video
    private String status;        // active / ended

    private LocalDateTime createdOn;
}