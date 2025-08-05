package com.annular.filmhook.model;

import java.util.Date;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "InAppNotification")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InAppNotification {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "in_app_notification_id")
    private Integer inAppNotificationId;
    
    @Column(name = "senderId")
    private Integer senderId;
    
    @Column(name = "receiverId")
    private Integer receiverId;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "message")
    private String message;
    
    @Column(name = "createdOn")
    private Date createdOn;
    
    @Column(name = "isRead")
    private Boolean isRead;
    
    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updatedby")
    private Integer updatedBy;
    
    @Column(name = "updatedOn")
    private Date updatedOn;

    @Column(name = "userType")
    private String userType;
    
    @Column(name = "id")
    private Integer id;

    @Column(name = "postId")
    private String postId;
    
    @Column(name = "currentStatus")
    private Boolean currentStatus;
    
    @Column(name = "Profession")
    private String Profession;
    
    @Column(name = "admin_review")
    private Float adminReview;
    
  
    
}
