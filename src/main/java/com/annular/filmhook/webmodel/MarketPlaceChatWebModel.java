package com.annular.filmhook.webmodel;

import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MarketPlaceChatWebModel {
	

	    private Integer marketPlaceChatId;
	    private Integer marketPlaceSenderId;
	    private Integer marketPlaceReceiverId;
	    private String message;
	    private Boolean marketPlaceIsActive;
	    private Integer marketPlaceCreatedBy;
	    private Integer marketPlaceUpdatedBy;
	    private Date marketPlaceCreatedOn;
	    private Date marketPlaceUpdatedOn;
	    private String marketType;
	    private Date timeStamp;
	    List<MultipartFile> files;
	    List<FileOutputWebModel> chatFiles;
	    private Integer userId;
	    private Boolean accept;
	    private String senderProfilePic;
	    private String receiverProfilePic;
	    private String userType;
	    private String userAccountName;
	    private String receiverAccountName;
	    
	}


