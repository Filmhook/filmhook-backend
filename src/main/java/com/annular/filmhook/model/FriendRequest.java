package com.annular.filmhook.model;

import java.sql.Date;
import java.util.List;

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
@Table(name = "frientRequest")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "friend_request_id")
	private Integer friendRequestId;

	@Column(name = "friend_request_senderId")
	private Integer frientRequestSenderId; // user table userId

	@Column(name = "frient_request_receiver_id")
	private Integer friendRequestReceiverId; // user table userId

	@Column(name = "requeststaSendertus")
	private String friendRequestSenderStatus;

	@Column(name = "friend_Request_is_active")
	private Boolean friendRequestIsActive;

	@Column(name = "friend_Request_created_by")
	private Integer friendRequestCreatedBy;

	@Column(name = "friend_Request_updated_by")
	private Integer friendRequestUpdatedBy;

	@CreationTimestamp
	@Column(name = "friend_Request_created_on")
	private Date friendRequestCreatedOn;

	@CreationTimestamp
	@Column(name = "friend_Request_updated_on")
	private Date friendRequestUpdatedOn;

	@Column(name = "user_type")
	private String userType;

}
