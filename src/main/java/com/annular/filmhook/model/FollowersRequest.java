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
@Table(name = "follwersRequest")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FollowersRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "follwers_request_id")
	private Integer follwersRequestId;

	@Column(name = "follwers_request_senderId")
	private Integer follwersRequestSenderId; // user table userId

	@Column(name = "follwers_request_receiver_id")
	private Integer follwersRequestReceiverId; // user table userId

	@Column(name = "follwers_request_status")
	private String follwersRequestSenderStatus;

	@Column(name = "follwers_request_is_active")
	private Boolean follwersRequestIsActive;

	@Column(name = "follwers_request_created_by")
	private Integer follwersRequestCreatedBy;

	@Column(name = "follwers_request_updated_by")
	private Integer follwersRequestUpdatedBy;

	@CreationTimestamp
	@Column(name = "follwers_request_created_on")
	private Date follwersRequestCreatedOn;

	@CreationTimestamp
	@Column(name = "follwers_request_updated_on")
	private Date follwersRequestUpdatedOn;

	@Column(name = "user_type")
	private String userType;

}
