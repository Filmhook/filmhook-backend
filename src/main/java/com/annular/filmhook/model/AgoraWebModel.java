package com.annular.filmhook.model;

import lombok.Data;

@Data
public class AgoraWebModel {

    // RTC
    private String channelName;
    private Integer uid;
    private Integer expirationTimeInSeconds;
    private Integer role;

    // RTM
    private Integer userId;
}
