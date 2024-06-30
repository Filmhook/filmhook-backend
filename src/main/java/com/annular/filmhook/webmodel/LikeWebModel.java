package com.annular.filmhook.webmodel;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeWebModel {

    private Integer likeId;
    private Integer userId; // primary key of user table
    private Integer postId; // primary key of posts table
    private Integer totalLikesCount;

    private String category; // 'Post' or 'Comment'
    private Integer commentId; // Comment primary key. If like is for any comment.

    private Boolean status;

    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer likedBy;

}
