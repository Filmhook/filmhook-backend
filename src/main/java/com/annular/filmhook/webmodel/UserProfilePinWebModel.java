package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfilePinWebModel {

    private Integer userProfilePinId;
    private Integer userMediaPinId;
    private Integer userId;
    private Integer pinMediaId;
    private boolean status;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer flag;
    private Integer pinProfileId;

}
