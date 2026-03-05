package com.annular.filmhook.webmodel;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class CallHistoryResponse {

    private Integer userId;
    private String userName;
    private String profilePicUrl;

    private String groupName;
    private Boolean groupCall;
    private List<Integer> groupUserIds; 

    private String callType;     // voice / video
    private String direction;    // incoming / outgoing
    private String status;       // completed / missed / rejected

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Long durationSeconds;
}