package com.annular.filmhook.webmodel;

import java.util.Date;

import com.annular.filmhook.model.User;

import lombok.Data;

@Data
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
