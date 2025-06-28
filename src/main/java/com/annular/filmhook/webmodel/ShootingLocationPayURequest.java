package com.annular.filmhook.webmodel;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

@Data
public class ShootingLocationPayURequest {
	private Integer paymentId;
	private Integer bookingId;
	private String txnid;
	private String amount;
	private String productinfo;
	private String firstname;
	private String email;
	private Integer createdBy;
	private LocalDateTime createdOn;
	private Integer updatedBy;
	private Date updatedOn;
	private String status;
	private String reason;
	private String phone;
}
