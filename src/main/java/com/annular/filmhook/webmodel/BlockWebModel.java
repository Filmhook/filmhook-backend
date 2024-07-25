package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlockWebModel {

    private Integer blockId;

    private Integer blockedBy; //foreign key for user table
    private Integer blockedUser; //foreign key for userTable
    private String blockStatus; // 'Blocked' or 'UnBlocked'

    private String blockedUserName;
    private String blockedUserGender;
    private String blockedUserProfilePicUrl;

    private Boolean status;

    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private String userType;
    private Float review;

}
