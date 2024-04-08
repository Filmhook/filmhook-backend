package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Data;

@Data
public class ReportPostWebModel {

    private Integer reportPostId;;
    private Integer userId;//foreign key for user table 
    private Integer postId;//foreign key for media files
    private String reason;
    private Boolean status;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;

}
