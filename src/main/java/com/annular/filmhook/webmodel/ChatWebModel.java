package com.annular.filmhook.webmodel;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

	List<MultipartFile> files;
	List<FileOutputWebModel> chatFiles;

}
