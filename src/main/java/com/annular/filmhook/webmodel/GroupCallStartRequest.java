package com.annular.filmhook.webmodel;

import java.util.List;

import lombok.Data;

@Data
public class GroupCallStartRequest {
    private Integer hostUserId;
    private List<Integer> memberIds;
    private String callType;
    private String hostName;
    private String hostPic;
    
    private String channelName; 
}
