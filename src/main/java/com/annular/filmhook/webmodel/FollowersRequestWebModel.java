package com.annular.filmhook.webmodel;

import java.sql.Date;




import lombok.Data;

@Data
public class FollowersRequestWebModel {

	
	private Integer follwersRequestId;
	private Integer follwersRequestSenderId; // user table userId
	private Integer follwersRequestReceiverId; // user table userId
	private String follwersRequestSenderStatus;
	private Boolean follwersRequestIsActive;
	private Integer follwersRequestCreatedBy;
	private Integer follwersRequestUpdatedBy;
	private Date follwersRequestCreatedOn;
	private Date follwersRequestUpdatedOn;
	private String userType;
}

