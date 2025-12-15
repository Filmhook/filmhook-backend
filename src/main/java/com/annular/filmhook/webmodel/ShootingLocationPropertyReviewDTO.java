package com.annular.filmhook.webmodel;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShootingLocationPropertyReviewDTO {
	  private Integer id;
	private Integer propertyId;
    private Integer userId;
     private String userName;
    private String profilePicUrl;
    private int rating;
    private String reviewText;
    private LocalDateTime createdOn;
   private List<FileOutputWebModel> files; 
    private List<MultipartFile> file; 
    
    private String ownerReplyText;
    private Integer ownerReplyBy;   // property owner userId
    private LocalDateTime ownerReplyOn;
    private String ownerReplyByName;
}
