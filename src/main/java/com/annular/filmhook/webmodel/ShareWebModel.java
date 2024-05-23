package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShareWebModel {

    private Integer shareId;
    private Integer userId; // primary key of user table
    private Integer postId; // primary key of posts table
    private Integer totalSharesCount;

    private Boolean status;

    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;

}
