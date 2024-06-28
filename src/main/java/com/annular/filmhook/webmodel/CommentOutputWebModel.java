package com.annular.filmhook.webmodel;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentOutputWebModel {

    private Integer commentId;
    private Integer userId; // primary key of user table
    private Integer postId; // primary key of posts table
    private String content;

    private String category; // 'Post' or 'Comment'
    private Integer parentCommentId; // Comment primary key. If comment is for any parent comment.

    private Integer totalLikesCount;
    private Integer totalCommentCount;

    private Boolean status;
    private String time;

    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;

    private String userProfilePic;
    private String userName;

    private List<CommentOutputWebModel> childComments;
}
