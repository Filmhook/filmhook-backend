package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Data;

@Data
public class LikeWebModel {
	
    private Integer likeId;;
    private Integer userId;//foreign key for user table 
    private Integer postId;//foreign key for userTable 
    private Boolean status;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;


}
