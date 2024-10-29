package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketPlaceUserWebModel {
	
	 private Integer userId; // User table primary key
	    private String userName; // User's Name
	    private String userType;
	    private String profilePicUrl;
	    private String latestMessage;
	    private Date latestMsgTime;
	    private int receiverUnreadCount;
	    private Float adminReview;
	    private Boolean onlineStatus;

}
