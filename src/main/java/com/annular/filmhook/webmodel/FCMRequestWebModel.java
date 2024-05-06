package com.annular.filmhook.webmodel;

import java.util.Map;

import lombok.Data;

@Data
public class FCMRequestWebModel {

	private String fCMToken;
    private String userName;
    private String callType;
    private String userId;
    private String channelName;
    private String channelToken;
   
}
