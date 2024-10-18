package com.annular.filmhook.webmodel;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

import com.annular.filmhook.model.MediaFileCategory;

import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostWebModel {

    private Integer userId;
    private String userName;

    private Integer id; // Primary key of post-table
    private String postId; // unique string for each post
    private MediaFileCategory category;
    private String description;
    List<MultipartFile> files;

    private Integer likeCount;
    private Integer commentCount;
    private Integer shareCount;
    private Boolean promoteFlag;

    private List<FileOutputWebModel> postFiles;
    private Set<String> professionNames;
    private String userProfilePic;
    private String locationName;
    private Boolean privateOrPublic;
    private String postUrl;
    private Integer followersCount;
    private Boolean likeStatus;
    private Integer likeId;
    private Boolean pinStatus;
    private String elapsedTime;

    private Date createdOn;
    private Integer createdBy;
    private String postLinkUrl;

    private List<Integer> taggedUsers;
    private List<Map<String, Object>> taggedUserss;
    private Integer pageNo;
    private Integer pageSize;
    private String latitude;
    private String longitude;
    private String address;
    private List<Integer> mediaFilesIds;
    private String userType;
    private Float adminReview;
    private Boolean promoteStatus;
    private Integer promoteId;
    private Integer numberOfDays;
    private Integer amount;
    private String whatsAppNumber;
    private String webSiteLink;
    private Integer selectOption;
}
