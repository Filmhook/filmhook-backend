package com.annular.filmhook.webmodel;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StoriesWebModel {

    private Integer userId;
    private String userName;
    private String storyId;
    private String type;
    private String description;
    private FileInputWebModel fileInputWebModel; // for file input details
    private List<FileOutputWebModel> fileOutputWebModel; // for file output details

    private Integer viewCount;

    private Boolean status;

    private Date createdOn;
    private Integer createdBy;

}
