package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FollowersRequestWebModel {

    private Integer followersRequestId;

    private Integer followersRequestSenderId; // user table userId
    private Integer followersRequestReceiverId; // user table userId
    private String followersRequestStatus;
    private Boolean followersRequestIsActive;

    private Integer followersRequestCreatedBy;
    private Integer followersRequestUpdatedBy;
    private Date followersRequestCreatedOn;
    private Date followersRequestUpdatedOn;

    private String userName;
    private String userGender;
    private String userType;
    private String userProfilePicUrl;

}

