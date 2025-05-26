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
@Table(name = "shootingLocationChat")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ShootingLocationChat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "shooting_location_chatId")
	private Integer shootingLocationChatId;

	@Column(name = "shooting_location_sender_id")
	private Integer shootingLocationSenderId;

	@Column(name = "shooting_location_receiver_id")
	private Integer shootingLocationReceiverId;

	@Column(name = "message")
	private String message;

	@Column(name = "shooting_location_is_active")
	private Boolean shootingLocationIsActive;

	@Column(name = "shooting_location_created_by")
	private Integer shootingLocationCreatedBy;

	@Column(name = "shooting_location_updated_by")
	private Integer shootingLocationUpdatedBy;

	@CreationTimestamp
	@Column(name = "shooting_location_created_on")
	private Date shootingLocationCreatedOn;

	@CreationTimestamp
	@Column(name = "shooting_location_updated_on")
	private Date shootingLocationUpdatedOn;

	@CreationTimestamp
	@Column(name = "time_stamp")
	private Date timeStamp;

	@Column(name = "shootingLocationStartTime")
	private String shootingLocationStartTime;
	

	@Column(name = "shootingLocationEndTime")
	private String shootingLocationEndTime;
	
	
    @Column(name = "accept")
    private Boolean accept;

}