package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentInputWebModel {

	private Integer commentId;
	private Integer userId; // primary key of user table
	private Integer postId; // primary key of posts table
	private String content;

	private String category; // 'Post' or 'Comment'
	private Integer parentCommentId; // Comment primary key. If comment is for any parent comment.

}
