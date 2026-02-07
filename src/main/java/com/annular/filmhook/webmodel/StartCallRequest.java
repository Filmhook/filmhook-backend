package com.annular.filmhook.webmodel;

import lombok.Data;

@Data
public class StartCallRequest {
    private Integer callerId;
    private Integer receiverId;
    private String callType;   // audio | video
}
