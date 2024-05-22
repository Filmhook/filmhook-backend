package com.annular.filmhook.webmodel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Data
public class ShareWebModel {

    private Integer shareId;
    private Integer userId; // primary key of user table
    private Integer postId; // primary key of posts table
    private Boolean status;
    private Integer createdBy;
    private Date createdOn;
    private Integer updatedBy;
    private Date updatedOn;
    private String postUrl;  


}
