package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentWebModel {

	private Integer commentId;
	private Integer userId; // primary key of user table
	private Integer postId; // primary key of posts table
	private String content;
	private Long totalCommentCount;

	private Boolean status;

	private Integer createdBy;
	private Date createdOn;
	private Integer updatedBy;
	private Date updatedOn;
	private String userProfilePic;
	private String userName;
}
