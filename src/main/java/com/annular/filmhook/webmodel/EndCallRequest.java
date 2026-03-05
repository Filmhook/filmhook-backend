package com.annular.filmhook.webmodel;

import lombok.Data;

@Data
public class EndCallRequest {
    private String channelName;
    private Integer userId; // who ended
    private String status; // ended | rejected | missed | busy
    private String userName;
}

