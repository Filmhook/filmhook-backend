package com.annular.filmhook.webmodel;

import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@Builder
public class ShootingLocationChatDTO {
	
	private Integer chatId;
    private Integer senderId;
    private Integer receiverId;
    private String message;
    private Date timeStamp;
    private String startTime;
    private String endTime;
    private List<FileOutputWebModel> mediaFiles;
    private String senderProfilePic;
    private String receiverProfilePic;
    private String senderAccountName;            // Sender account name
    private String receiverAccountName;

}
