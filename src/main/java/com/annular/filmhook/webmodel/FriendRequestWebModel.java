package com.annular.filmhook.webmodel;

import java.sql.Date;



import lombok.Data;

@Data
public class FriendRequestWebModel {
	
	private Integer friendRequestId;
	private Integer frientRequestSenderId; // user table userId
	private Integer friendRequestReceiverId; // user table userId
	private String friendRequestStatus;
	private Boolean friendRequestIsActive;
	private Integer friendRequestCreatedBy;
	private Integer friendRequestUpdatedBy;
	private Date friendRequestCreatedOn;
	private Date friendRequestUpdatedOn;
	private String userType;

}
