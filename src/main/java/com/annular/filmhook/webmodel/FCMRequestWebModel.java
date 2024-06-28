package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FCMRequestWebModel {

    private String token;
    private String userName;
    private String callType;
    private String userId;
    private String channelName;
    private String channelToken;

}
