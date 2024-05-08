package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Data;

@Data
public class ChatWebModel {

	private Integer chatId;
	private Integer chatSenderId;
	private Integer chatReceiverId;
	private String message;
	private Boolean chatIsActive;
	private Integer chatCreatedBy;
	private Integer chatUpdatedBy;
	private Date chatCreatedOn;
	private Date chatUpdatedOn;
	private String userType;
	private Date timeStamp;
	private String userAccountName;
	private Integer userId;

}
