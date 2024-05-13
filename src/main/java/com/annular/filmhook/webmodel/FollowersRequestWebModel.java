package com.annular.filmhook.webmodel;

import java.sql.Date;




import lombok.Data;

@Data
public class FollowersRequestWebModel {

	
	private Integer followersRequestId;
	private Integer followersRequestSenderId; // user table userId
	private Integer followersRequestReceiverId; // user table userId
	private String followersRequestSenderStatus;
	private Boolean followersRequestIsActive;
	private Integer followersRequestCreatedBy;
	private Integer followersRequestUpdatedBy;
	private Date followersRequestCreatedOn;
	private Date followersRequestUpdatedOn;
	private String userType;
}

