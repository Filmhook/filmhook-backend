package com.annular.filmhook.webmodel;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonInclude;

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
    private String senderProfilePic;
    private String receiverProfilePic;
    private String receiverAccountName;
    private Boolean senderRead;
    private Boolean receiverRead;

    List<MultipartFile> files;
    List<FileOutputWebModel> chatFiles;
    
    //Pagination details
    private Integer pageNo;
    private Integer pageSize;
    private String storyId;
    private String storyMediaUrl;
    private String replyType;
    private String storyMediaType;
    private String deleteType;
    private Boolean isDeletedForEveryone;
    private Boolean edited;
    private Date editedOn;
       private Integer replyToMessageId;
       
       private ReplyMessageDTO replyToMessage;  // ✅ nested reply DTO

   
       @Data
       @AllArgsConstructor
       @NoArgsConstructor
       public static class ReplyMessageDTO {
           private Integer chatId;
           private Integer chatSenderId;
           private String message;
           private String userAccountName;
           private String mediaUrl;   
           private String mediaType; 
       }
   }
