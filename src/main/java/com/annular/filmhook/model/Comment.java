package com.annular.filmhook.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "comment")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
    private Integer commentId;

    @Column(name="category")
    private String category; // Post or Comment

    @Column(name = "post_Id")
	private Integer postId; // primary key of posts, userMediaPin, userProfilePin

    @Column(name = "parent_comment_Id", columnDefinition = "int default null")
    private Integer parentCommentId; // primary key of parent comment

    @Column(name = "commented_by")
    private Integer commentedBy; // primary key of user table

    @Column(name = "content")
    private String content;

    @Column(name = "likes_count", nullable = false, columnDefinition = "int default 0")
    private Integer likesCount;
    
    @Column(name = "status")
    private Boolean status;

    @Column(name = "created_by")
    private Integer createdBy;

    @CreationTimestamp
    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "updated_on")
    @CreationTimestamp
    private Date updatedOn;


}
