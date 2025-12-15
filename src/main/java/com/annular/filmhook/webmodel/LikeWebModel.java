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
    private Integer totalLikesCount;   // total number of likes
    private Integer totalUnlikesCount; // total number of unlikes

    private Integer auditionId;
    private String category; // 'Post' or 'Comment' or 'Audition'
    private Integer commentId; // Comment primary key. If like/unlike is for any comment.
    private Integer reviewId; 

    private Boolean status;   // true = like, false = unlike
    private Boolean isLiked;  // logged-in user’s like status
    private Boolean isUnliked; // logged-in user’s unlike status

    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private Integer likedBy; // who liked/unliked
    private String reactionType; 
}
