package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportPostWebModel {

    private Integer reportPostId;
    private Integer userId; // primary key of user table
    private Integer postId; // primary key of posts table
    private String reason;
    private Integer deletePostSuspension;
    private String postTitle;
    private String emailId;
    private String userName;
    private Date UploadDate;
    private String violationReason;

    private Boolean status;

    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;

    //Pagination details
    private Integer pageNo;
    private Integer pageSize;

}
