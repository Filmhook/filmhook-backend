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
@Table(name = "chat")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Integer chatId;

    @Column(name = "chat_sender_id")
    private Integer chatSenderId;

    @Column(name = "chat_receiver_id")
    private Integer chatReceiverId;

    @Column(name = "message")
    private String message;

    @Column(name = "chat_is_active")
    private Boolean chatIsActive;

    @Column(name = "chat_created_by")
    private Integer chatCreatedBy;

    @Column(name = "chat_updated_by")
    private Integer chatUpdatedBy;

    @CreationTimestamp
    @Column(name = "chat_created_on")
    private Date chatCreatedOn;

    @CreationTimestamp
    @Column(name = "chat_updated_on")
    private Date chatUpdatedOn;

    @Column(name = "user_type")
    private String userType;

    @CreationTimestamp
    @Column(name = "time_stamp")
    private Date timeStamp;

    @Column(name = "user_account_name")
    private String userAccountName;
    
    @Column(name = "userRead")
    private Boolean senderRead;
    
    @Column(name = "receiverRead")
    private Boolean receiverRead;
    
    private Boolean deletedBySender = false;
    private Boolean deletedByReceiver = false;
    
    @Column(name = "story_id")
    private String storyId;
    
    @Column(name = "reply_type")
    private String replyType;

}
