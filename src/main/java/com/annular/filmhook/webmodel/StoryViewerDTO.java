package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoryViewerDTO {
    private Integer viewerId;
    private String viewerName;
    private String userProfilePic;
    private Date viewedOn;
    private String viewedAtText; 
}