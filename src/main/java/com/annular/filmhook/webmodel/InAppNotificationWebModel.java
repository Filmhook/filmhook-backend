package com.annular.filmhook.webmodel;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InAppNotificationWebModel {


	private Integer inAppNotificationId;
	private Integer senderId;
	private Integer receiverId;
	private String title;
	private String message;
	private Date createdOn;
	private Boolean isRead;
	private Integer createdBy;
	private Integer updatedBy;
	private Date updatedOn;
	private String userType;
	private String profilePicUrl;
	private Integer id;
	private String postId;
	private String senderName;
	private Boolean accept;
	private String additionalData;
	private Boolean currentStatus;
	private String Profession;
	private Float adminReview;
	private List<Integer> ReceiverIds;
}
