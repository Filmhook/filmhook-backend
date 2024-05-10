package com.annular.filmhook.webmodel;

import java.util.Date;


import lombok.Data;

@Data
public class LiveDetailsWebModel {
	
	private Integer liveChannelId;
	private Integer userId;// foreign key for user table
	private String channelName;
	private Boolean liveIsActive;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private String token;
    private String startTime;
    private String endTime;
    private String liveDate;
    private String liveId;

}
