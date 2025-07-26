package com.annular.filmhook.webmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileOutputWebModel {

    private Integer id; // MySQL Primary key field

    private String fileId;
    private String fileName;
    private long fileSize;
    private String fileType;
    private String filePath;
    private String description;
    private String type;
    
    private Integer userId;
    private String category;
    private Integer categoryRefId;

    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;	
    private Date updatedOn;

    private String elapsedTime;
    private String filmHookCode;
    private Long duration;
    private Integer viewCount;
    private Integer likedCount;
    private List<StoryViewerDTO> viewedBy;
    private String storyId;
    private Boolean isStoryLiked;
    private String thumbnailPath;

}