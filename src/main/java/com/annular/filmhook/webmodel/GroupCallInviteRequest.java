package com.annular.filmhook.webmodel;

import java.util.List;

import lombok.Data;

@Data
public class GroupCallInviteRequest {
    private Integer groupCallId;
    private Integer hostUserId;
    private List<Integer> memberIds;
    private String channelName;
}
