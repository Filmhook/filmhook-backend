package com.annular.filmhook.webmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    private Set<String> professionNames; 
    private String profileUrl;
    private Date createdOn;
    private Integer createdBy;
    private String filePath;
    private Boolean seen;
    private String link;

}
