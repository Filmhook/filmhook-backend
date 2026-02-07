package com.annular.filmhook.model;

import java.time.LocalDateTime;

import javax.persistence.*;


import lombok.Data;

@Entity
@Table(name = "call_logs")
@Data
public class CallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer callerId;
    private Integer receiverId;

    private String channelName;
    private String callType;   // audio OR video
    private String status;     // initiated, accepted, ended, rejected, busy, missed

    @Column(columnDefinition = "TEXT")
    private String rtcToken;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
