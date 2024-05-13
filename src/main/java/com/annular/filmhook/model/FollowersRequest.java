package com.annular.filmhook.model;

import java.sql.Date;

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
@Table(name = "followersRequest")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FollowersRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "followers_request_id")
	private Integer followersRequestId;

	@Column(name = "followers_request_senderId")
	private Integer followersRequestSenderId; // user table userId

	@Column(name = "followers_request_receiver_id")
	private Integer followersRequestReceiverId; // user table userId

	@Column(name = "followers_request_status")
	private String followersRequestSenderStatus;

	@Column(name = "followers_request_is_active")
	private Boolean followersRequestIsActive;

	@Column(name = "followers_request_created_by")
	private Integer followersRequestCreatedBy;

	@Column(name = "followers_request_updated_by")
	private Integer followersRequestUpdatedBy;

	@CreationTimestamp
	@Column(name = "followers_request_created_on")
	private Date followersRequestCreatedOn;

	@CreationTimestamp
	@Column(name = "followers_request_updated_on")
	private Date followersRequestUpdatedOn;

	@Column(name = "user_type")
	private String userType;

}
