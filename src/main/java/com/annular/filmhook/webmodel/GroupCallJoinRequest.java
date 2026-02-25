package com.annular.filmhook.webmodel;

import lombok.Data;

@Data
public class GroupCallJoinRequest {
    private Integer groupCallId;
    private Integer userId;
}